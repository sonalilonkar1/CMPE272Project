import Stripe from 'stripe';
import { NextResponse } from 'next/server';

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY, {
  apiVersion: '2022-11-15',
});

export async function POST(request) {
  try {
    const { amount, charityId, charityName, stripeAccount } = await request.json();

    // Create a Stripe Checkout Session
    const session = await stripe.checkout.sessions.create({
      payment_method_types: ['card'],
      line_items: [
        {
          price_data: {
            currency: 'usd',
            product_data: {
              name: `Donation to ${charityName}`,
              description: `Your generous donation to ${charityName}`,
            },
            unit_amount: amount, // amount in cents
          },
          quantity: 1,
        },
      ],
      mode: 'payment',
      success_url: `${process.env.NEXT_PUBLIC_BASE_URL}/donor?payment=success`,
      cancel_url: `${process.env.NEXT_PUBLIC_BASE_URL}/donor?payment=cancelled`,
      metadata: {
        charityId,
        charityName,
      },
    }, stripeAccount ? { stripeAccount } : undefined);
    console.log("session",session)
    return NextResponse.json({ url: session.url, sessionId: session.id });
  } catch (error) {
    // console.error('Stripe error:', error);
    return NextResponse.json(
      { message: error.message || 'Failed to create payment' },
      { status: 500 }
    );
  }
} 