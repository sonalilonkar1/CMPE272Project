import { NextResponse } from "next/server";
import axios from "axios";

// Your backend API URL
const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api";

/**
 * API handler for user registration
 * Acts as a proxy to your Java backend
 */
export async function POST(request) {
  try {
    // Parse the request body
    const body = await request.json();
    
    // Forward the registration request to your Java backend
    const response = await axios.post(`${API_URL}/auth/register`, body);
    
    // Return the response from your backend
    return NextResponse.json(response.data);
  } catch (error) {
    console.error("Registration error:", error);
    
    // Handle error responses from the backend
    if (error.response) {
      return NextResponse.json(
        { 
          success: false,
          message: error.response.data.message || "Registration failed" 
        },
        { status: error.response.status }
      );
    }
    
    // Handle network or other errors
    return NextResponse.json(
      { 
        success: false,
        message: "An error occurred during registration" 
      },
      { status: 500 }
    );
  }
} 