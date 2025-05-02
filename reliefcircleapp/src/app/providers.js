'use client'
import { Provider } from 'react-redux';
import { store } from '@/redux/store';
import { SessionProvider } from "next-auth/react";


export function Providers({ children, session }) {
  return (
    <SessionProvider session={session}>
      <Provider store={store}>
        {children}
      </Provider>
    </SessionProvider>
  );
} 