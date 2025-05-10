import Stripe from 'stripe';
import { NextResponse } from 'next/server';

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);
const API_URL = process.env.API_URL;

export async function POST(request) {
  try {
    // Get query params from the request URL
    const { searchParams } = new URL(request.url);
    const fundraiserId = searchParams.get('fundraiserId');

    // Optionally, validate fundraiserId here

    // Create a Stripe account
    const account = await stripe.accounts.create({
      type: 'standard',
    });

    // Save the Stripe account ID to your backend
    if (API_URL && fundraiserId) {
      await fetch(`${API_URL}/users/accountupdate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fundraiserId, stripeAccountId: account.id }),
      });
    }

    // Create an account link for onboarding
    const accountLink = await stripe.accountLinks.create({
      account: account.id,
      refresh_url: 'http://localhost:3000/fundraiser?tab=transactions&stripe=refresh',
      return_url: 'http://localhost:3000/fundraiser?tab=transactions&stripe=success',
      type: 'account_onboarding',
    });

    return NextResponse.json({ url: accountLink.url, accountId: account.id });
  } catch (error) {
    console.error('Stripe error:', error);
    return NextResponse.json({ error: error.message }, { status: 500 });
  }
}
