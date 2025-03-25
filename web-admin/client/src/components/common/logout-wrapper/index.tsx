'use client';

import { signOut } from 'next-auth/react';

type TLogoutWrapperProps = {
    children: React.ReactNode;
};

function LogoutWrapper(props: TLogoutWrapperProps) {
    const { children } = props;
    return (
        <div
            onClick={async () => {
                await signOut({ redirect: true, callbackUrl: '/login' });
            }}>
            {children}
        </div>
    );
}

export default LogoutWrapper;
