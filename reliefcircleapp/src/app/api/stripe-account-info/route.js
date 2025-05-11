import { NextResponse } from 'next/server';
import Stripe from 'stripe';

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);

export async function GET(request) {
  const { searchParams } = new URL(request.url);
  const stripeId = searchParams.get('stripeId');
  if (!stripeId) {
    return NextResponse.json({ error: 'Missing stripeId' }, { status: 400 });
  }
  try {
    const account = await stripe.accounts.retrieve(stripeId);

    // Get the latest 10 charges for this account (Express/Custom only)
    let charges = [];
    try {
      const chargesResult = await stripe.charges.list(
        { limit: 10 },
        { stripeAccount: stripeId }
      );
      charges = chargesResult.data.map(charge => ({
        id: charge.id,
        amount: charge.amount,
        currency: charge.currency,
        status: charge.status,
        description: charge.description,
        created: charge.created,
        receipt_url: charge.receipt_url,
        customer: charge.customer,
        payment_method_details: charge.payment_method_details,
      }));
    } catch (err) {
      charges = [];
    }

    // Get the balance for this account (Express/Custom only)
    let balance = null;
    try {
      const balanceResult = await stripe.balance.retrieve(
        { },
        { stripeAccount: stripeId }
      );
      balance = balanceResult;
    } catch (err) {
      balance = null;
    }

    return NextResponse.json({
      id: account.id,
      email: account.email,
      type: account.type,
      country: account.country,
      business_type: account.business_type,
      created: account.created,
      capabilities: account.capabilities,
      business_profile: account.business_profile,
      charges,
      balance,
    });
  } catch (error) {
    return NextResponse.json({ error: error.message }, { status: 500 });
  }
}