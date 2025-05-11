import Stripe from 'stripe';
import { NextResponse } from 'next/server';
import { getServerSession } from "next-auth";
import { authOptions } from "@/app/api/auth/[...nextauth]/route";

const stripe = new Stripe(process.env.STRIPE_SECRET_KEY);
const API_URL = process.env.API_URL;

export async function POST(request) {
  const session = await getServerSession(authOptions);
  const token = session?.accessToken; // or session?.user?.token, depending on your setup

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
    // #TODO: this isnt working right now
    if (API_URL && fundraiserId) {
      await fetch(`${API_URL}/users/me/stripe-id`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ "stripeId" :  account.id }),
      });
    }

    console.log("account.id",account.id);

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
