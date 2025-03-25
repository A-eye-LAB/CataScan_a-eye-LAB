/* @see https://authjs.dev/getting-started/typescript#extend-default-interface-properties */
import { DefaultSession, DefaultUser } from 'next-auth';

declare module 'next-auth' {
    interface User extends DefaultUser {
        token: string;
    }

    interface Session {
        user: {
            id: string;
            role: string;
            token: string;
            exp: number;
            realExp: number;
        } & DefaultSession['user'];
    }
}
