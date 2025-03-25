'use client';

import { useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { Loader2 } from 'lucide-react';
import { z } from 'zod';
import { signIn } from 'next-auth/react';
import { zodResolver } from '@hookform/resolvers/zod';
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

const formSchema = z.object({
    id: z.string().min(1, { message: 'ID is required' }),
    password: z.string().min(1, { message: 'Password is required' }),
});

function LoginForm() {
    const searchParams = useSearchParams();

    const [isLoading, setIsLoading] = useState(false);

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            id: '',
            password: '',
        },
    });

    const onSubmit = async (values: z.infer<typeof formSchema>) => {
        const { id, password } = values;
        setIsLoading(true);

        try {
            await signIn('login-credentials', {
                username: id,
                password: password,
                callbackUrl: '/reports',
            });
        } catch (error) {
            console.error(error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)}>
                <div className={'flex flex-col gap-2'}>
                    <FormField
                        control={form.control}
                        name="id"
                        render={({ field }) => (
                            <FormItem>
                                <FormControl>
                                    <Input
                                        id="id"
                                        placeholder="Enter the Center ID"
                                        autoComplete={'current-password'}
                                        maxLength={20}
                                        {...field}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="password"
                        render={({ field }) => (
                            <FormControl>
                                <Input
                                    id="password"
                                    type="password"
                                    placeholder="Enter the Password"
                                    autoComplete={'current-password'}
                                    {...field}
                                />
                            </FormControl>
                        )}
                    />
                    <Button
                        variant={'catascan'}
                        disabled={isLoading}
                        type="submit"
                        className="
                            w-full
                    ">
                        {isLoading ? (
                            <>
                                <Loader2 className={'animate-spin'} />
                                Please wait
                            </>
                        ) : (
                            'Sign In'
                        )}
                    </Button>
                    {searchParams.get('error') === 'CredentialsSignin' && (
                        <p className="text-red-500 text-sm text-center">
                            Incorrect ID or password. Please try again.
                        </p>
                    )}
                </div>
            </form>
        </Form>
    );
}

export default LoginForm;
