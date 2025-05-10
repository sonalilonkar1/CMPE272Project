import Stripe from 'stripe';

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

export default async function handler(req, res) {
  const { fundraiserId } = req.query;

  // Create a Stripe account link
  const account = await stripe.accounts.create({
    type: 'standard',
  });

  const accountLink = await stripe.accountLinks.create({
    account: account.id,
    refresh_url: 'http://localhost:3000/onboarding/refresh',
    return_url: 'http://localhost:3000/onboarding/success',
    type: 'account_onboarding',
  });

  // Save the fundraiser's Stripe account ID in your DB
  // Example: await saveStripeAccount(fundraiserId, account.id);

  res.status(200).json({ url: accountLink.url });
}
