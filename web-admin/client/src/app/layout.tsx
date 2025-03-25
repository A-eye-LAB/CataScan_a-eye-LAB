import type { Metadata } from 'next';
import localFont from 'next/font/local';
import './globals.css';
import AuthProvider from '@/context/auth-provider';
import { Toaster } from '@/components/ui/sonner';

const pretendard = localFont({
    src: './fonts/PretendardVariable.woff2',
    variable: '--font-pretendard',
    weight: '400 500 600',
});

export const metadata: Metadata = {
    title: 'CataScan Admin',
    description: 'Admin for CataScan',
};

export default function RootLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html lang="en">
            <body className={`${pretendard.variable} antialiased`}>
                <AuthProvider>{children}</AuthProvider>
                <Toaster />
            </body>
        </html>
    );
}
