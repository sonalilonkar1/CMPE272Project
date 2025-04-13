import './globals.css';
import { ReduxProvider } from '@/redux/provider';
import Header from '@/components/layout/Header';
import Footer from '@/components/layout/Footer';

export const metadata = {
  title: 'ReliefCircle - Community-Driven Relief Fund',
  description: 'A transparent and decentralized fundraising platform',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <ReduxProvider>
          <div className="flex flex-col min-h-screen">
            <Header />
            <main className="flex-grow">{children}</main>
            {/* <Footer /> */}
          </div>
        </ReduxProvider>
      </body>
    </html>
  );
}