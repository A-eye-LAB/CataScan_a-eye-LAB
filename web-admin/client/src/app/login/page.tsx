import React, { Suspense } from 'react';
import Link from 'next/link';
import { getServerSession } from 'next-auth';
import { redirect } from 'next/navigation';
import LoginForm from '@/components/login/login-form';
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card';
import {
    EyeLogoIcon,
    TechForImpactLogo,
    TextLogoIcon,
} from '@/components/ui/icon';
import { authOptions } from '@/lib/utils/authOptions';

async function LoginPage() {
    const session = await getServerSession(authOptions);

    if (session?.user?.token) {
        redirect('/reports');
    }

    return (
        <div className="flex min-h-screen bg-white items-centerw-full">
            <div className={`relative hidden w-1/2 lg:block  `}>
                {/*<GradientBg className="absolute top-0 left-0 w-full h-full" />*/}
                <div
                    className={
                        'flex items-center justify-center w-full h-full'
                    }>
                    <div className="bg-[hsla(205,81%,70%,1)] relative w-full h-full">
                        <div className="absolute inset-0">
                            <div className="absolute w-[100%] h-[100%] bg-[radial-gradient(at_80%_100%,hsla(212,61%,30%,1)_0px,transparent_50%)]"></div>
                            <div className="absolute w-[100%] h-[100%] bg-[radial-gradient(at_80%_0%,hsla(189,100%,56%,1)_0px,transparent_50%)]"></div>
                            <div className="absolute w-[100%] h-[100%] bg-[radial-gradient(at_0%_0%,hsla(156,61%,88%,1)_0px,transparent_50%)]"></div>
                            <div className="absolute w-[100%] h-[100%] bg-[radial-gradient(at_40%_20%,hsla(188,100%,40%,1)_0px,transparent_50%)]"></div>
                        </div>
                        <div className="relative z-10 flex items-center justify-center min-h-screen">
                            <div className={'flex items-center'}>
                                <EyeLogoIcon
                                    width={100}
                                    height={100}
                                    color={'#ffffff'}
                                />
                                <TextLogoIcon
                                    width={225}
                                    height={71}
                                    color={'#ffffff'}
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="w-full lg:w-1/2">
                <div className="relative flex flex-col items-center justify-around h-full">
                    <div className="h-[80px]"></div>
                    <Card className="w-full max-w-md border-0 shadow-none">
                        <CardHeader className="flex items-center justify-center gap-y-3">
                            <CardTitle className={'text-2xl font-semibold'}>
                                Sign-in
                            </CardTitle>
                            <CardDescription
                                className={'text-sm'}
                                style={{ marginTop: '0' }}>
                                Please enter your user ID to continue.
                            </CardDescription>
                        </CardHeader>
                        <CardContent>
                            <Suspense fallback={null}>
                                <LoginForm />
                            </Suspense>
                        </CardContent>
                        <CardFooter>
                            <CardDescription
                                className={
                                    'text-center whitespace-break-spaces w-full'
                                }>
                                <span>
                                    {'By clicking continue, you agree to\nour '}
                                </span>
                                <Link
                                    href={
                                        'https://catascan.org/terms-of-service-partner'
                                    }
                                    className={'underline'}>
                                    {'Terms of Use (Institution)'}
                                </Link>
                                {' and '}
                                <Link
                                    href={'https://catascan.org/privacy-policy'}
                                    className={'underline'}>
                                    {'Privacy Policy.'}
                                </Link>
                            </CardDescription>
                        </CardFooter>
                    </Card>
                    <div className="pt-4 flex flex-col gap-y-5 items-center">
                        <div>
                            <TechForImpactLogo />
                        </div>
                        <div className="whitespace-break-spaces text-muted-foreground text-sm flex flex-col items-center">
                            <span>
                                {
                                    'This service was developed with support from kakaoimpact Foundation'
                                }
                            </span>
                            <span>
                                {
                                    'and contributions from the Tech for Impact community'
                                }
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;
