import { NextAuthOptions, Session, SessionStrategy, User } from 'next-auth';
import { AdapterUser } from 'next-auth/adapters';
import { JWT } from 'next-auth/jwt';
import CredentialsProvider, {
    CredentialsConfig,
} from 'next-auth/providers/credentials';
import { decrypt } from 'paseto-ts/v4';
import apiManager from '@/lib/api/apiManager';

const credentialsProviderOption: CredentialsConfig = {
    type: 'credentials',
    id: 'login-credentials',
    name: 'login-credentials',
    credentials: {
        username: { label: 'Username', type: 'text' },
        password: { label: 'Password', type: 'password' },
    },
    async authorize(credentials: Record<string, unknown> | undefined) {
        if (!credentials) {
            return null;
        }

        if (
            typeof credentials.username === 'string' &&
            typeof credentials.password === 'string'
        ) {
            try {
                const result = await apiManager.login({
                    username: credentials.username,
                    password: credentials.password,
                });

                const token = result.data.token;

                return {
                    id: credentials.username as string,
                    token,
                };
            } catch (error) {
                console.error('Error in authorize:', error);
            }
        }

        console.error('Invalid credentials format');
        return null;
    },
};

export const authOptions: NextAuthOptions = {
    debug: true,
    pages: {
        signIn: '/login',
        error: '/login',
    },
    secret: process.env.NEXTAUTH_SECRET,
    session: {
        strategy: 'jwt' as SessionStrategy,
    },
    jwt: {
        secret: process.env.NEXTAUTH_SECRET,
    },
    providers: [CredentialsProvider(credentialsProviderOption)],
    callbacks: {
        async jwt({ token, user }: { token: JWT; user?: User | AdapterUser }) {
            if (user) {
                try {
                    const encodeResult = new TextEncoder().encode(
                        process.env.PASETO_KEY!
                    );
                    const decodedValue = decrypt(encodeResult, user.token);

                    token.id = decodedValue.payload.userId;
                    token.role = decodedValue.payload.role;
                    token.exp = decodedValue.payload.expiryDate;
                    token.realExp = decodedValue.payload.expiryDate;
                    token.value = user.token;
                    token.refreshToken = user.token;
                } catch (error) {
                    console.error('Error decoding token: ', error);
                }
            }
            const current = Date.now() / 1000; // 밀리초(ms) -> 초(s) 변환

            // To use the expiryDate value from the token received from the login API,
            // the 'realExp' custom property is used instead of the default 'exp' property.
            if (token?.realExp && (token.realExp as number) < current) {
                try {
                    if (token?.refreshToken) {
                        const refreshedToken =
                            await apiManager.refreshAccessToken({
                                refreshToken: token?.refreshToken as string,
                            });

                        if (refreshedToken.data?.token) {
                            const encodeResult = new TextEncoder().encode(
                                process.env.PASETO_KEY!
                            );
                            const decodedValue = decrypt(
                                encodeResult,
                                refreshedToken.data.token
                            );

                            token.value = refreshedToken.data.token;
                            token.realExp = Math.floor(
                                decodedValue.payload.expiryDate
                            );
                            token.refreshToken = refreshedToken.data.token;
                        } else {
                            throw new Error('Failed to refresh token');
                        }
                    }
                } catch (error) {
                    console.error('Error refreshing token:', error);
                    return {};
                }
            }
            return token;
        },
        async session({ session, token }: { session: Session; token: JWT }) {
            // console.log('Session callback:', { session, token });

            session.user = {
                id: token.id as string,
                token: token?.value as string,
                name: (token?.username ?? token?.sub) as string,
                exp: token?.exp as number,
                role: token?.role as string,
                realExp: token?.realExp as number,
            };
            return session;
        },
    },
};
