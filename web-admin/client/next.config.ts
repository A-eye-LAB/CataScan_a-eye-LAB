import type { NextConfig } from 'next';

const nextConfig: NextConfig = {
    /* config options here */
    async rewrites() {
        return [
            {
                source: '/api/auth/:path*',
                destination: '/api/auth/:path*', // NextAuth 경로는 리다이렉트하지 않음
            },
            {
                source: '/api/v1/:path*',
                destination: `${process.env.NEXT_PUBLIC_API_URL}/:path*`,
            },
        ];
    },
    images: {
        remotePatterns: [
            {
                protocol: 'http',
                hostname: `${process.env.NEXT_PUBLIC_HOSTNAME}`,
                port: '8080',
                pathname: '/reports/images/**',
            },
            {
                protocol: 'http',
                hostname: `${process.env.NEXT_PUBLIC_IMAGE_HOSTNAME}`,
                port: '8080',
                pathname: '/reports/images/**',
            },
        ],
    },
};

export default nextConfig;
