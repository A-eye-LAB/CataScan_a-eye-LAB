import React from 'react';
import { Controller, UseFormReturn, FormProvider } from 'react-hook-form';
import { Loader2 } from 'lucide-react';
import AutocompleteInput from '@/components/common/input/autocomplete-input';
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import {
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { TDialogProps } from '@/lib/types/ui';
import { UserAccountForm } from '@/lib/types/schema';
import { ACCOUNT } from '@/lib/constants';

type TEditUserDialogProps = TDialogProps & {
    mode: (typeof ACCOUNT.DIALOG_MODE)[number];
    onSubmit: (data: UserAccountForm) => void;
    isSubmitting?: boolean;
    form: UseFormReturn<UserAccountForm>;
};

function AccountDialog(props: TEditUserDialogProps) {
    const {
        isOpenDialog,
        setIsOpenDialog,
        mode,
        onSubmit,
        isSubmitting,
        form,
    } = props;

    return (
        <AlertDialog
            open={isOpenDialog}
            onOpenChange={() => {
                form.reset();
                setIsOpenDialog(!isOpenDialog);
            }}>
            <AlertDialogContent>
                <FormProvider {...form}>
                    <form>
                        <AlertDialogHeader>
                            <AlertDialogTitle
                                className={
                                    'text-CATASCAN-text-basic-foreground text-2xl'
                                }>
                                {`${mode} User Account`}
                            </AlertDialogTitle>
                            <AlertDialogDescription className={'sr-only'} />
                        </AlertDialogHeader>
                        <div className="min-h-[424px] mt-6">
                            <div className={'flex flex-col gap-4'}>
                                <FormField
                                    control={form.control}
                                    name="institutionName"
                                    render={() => (
                                        <FormItem>
                                            <FormLabel
                                                className={
                                                    'text-CATASCAN-text-basic-foreground'
                                                }>
                                                Institution *
                                            </FormLabel>
                                            <FormControl>
                                                <Controller
                                                    control={form.control}
                                                    name={'institutionName'}
                                                    render={({
                                                        field: {
                                                            value,
                                                            onChange,
                                                        },
                                                    }) => {
                                                        return (
                                                            <AutocompleteInput
                                                                id="institutionName"
                                                                placeholder="Institution"
                                                                autoComplete="off"
                                                                suggestions={
                                                                    ACCOUNT.INSTITUTION_SUGGESTIONS
                                                                }
                                                                value={value}
                                                                onChange={
                                                                    onChange
                                                                }
                                                            />
                                                        );
                                                    }}
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="username"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel
                                                className={
                                                    'text-CATASCAN-text-basic-foreground'
                                                }>
                                                User Name *
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    id="username"
                                                    placeholder="User Name"
                                                    autoComplete="off"
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
                                        <FormItem>
                                            <FormLabel
                                                className={
                                                    'text-CATASCAN-text-basic-foreground'
                                                }>
                                                Password *
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    id="password"
                                                    type="password"
                                                    placeholder="Password"
                                                    autoComplete="new-password"
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="confirmPassword"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel
                                                className={
                                                    'text-CATASCAN-text-basic-foreground'
                                                }>
                                                Confirm Password *
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    id="confirmPassword"
                                                    type="password"
                                                    placeholder="Confirm Password"
                                                    autoComplete="new-password"
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </div>

                        <AlertDialogFooter>
                            <AlertDialogCancel>{'Cancel'}</AlertDialogCancel>
                            <AlertDialogAction
                                variant={'catascan'}
                                type={'submit'}
                                disabled={isSubmitting}
                                onClick={(event) => {
                                    event.preventDefault();

                                    form.handleSubmit(onSubmit)();
                                }}>
                                {isSubmitting ? (
                                    <>
                                        <Loader2 className={'animate-spin'} />
                                        Please wait
                                    </>
                                ) : mode === 'Add' ? (
                                    'Add'
                                ) : (
                                    'Save'
                                )}
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </form>
                </FormProvider>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export default AccountDialog;
