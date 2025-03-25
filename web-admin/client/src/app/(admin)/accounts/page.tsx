'use client';

import React from 'react';
import { Loader2 } from 'lucide-react';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import AccountDialog from '@/components/accounts/account-dialog';
import useAccounts from '@/hooks/api/use-accounts';
import useDialog from '@/hooks/use-dialog';
import useUserManagement from '@/hooks/use-user-management';
import { userAccountsColumns } from '@/components/common/data-table/columns';
import DataTable from '@/components/common/data-table';
import ErrorUI from '@/components/common/error';
import { Button } from '@/components/ui/button';
import { UserAccountForm, userAccountFormSchema } from '@/lib/types/schema';
import { DEFAULT_VALUES } from '@/lib/constants';

function Accounts() {
    const { accounts, isLoading, error, mutate } = useAccounts();
    const { isOpen, setIsOpen } = useDialog(false);

    const form = useForm<UserAccountForm>({
        resolver: zodResolver(userAccountFormSchema),
        defaultValues: DEFAULT_VALUES.account,
    });

    const { isSubmitting, createUser } = useUserManagement(async () => {
        form.reset();
        setIsOpen(false);
        await mutate();
    });

    const onSubmit = async (data: UserAccountForm) => {
        await createUser(data);
    };

    return (
        <div>
            <div className={'flex justify-between mb-6'}>
                <h1 className={'text-3xl font-bold'}>User Accounts</h1>
                <div>
                    <Button
                        variant={'catascan'}
                        onClick={() => setIsOpen(true)}>
                        Add User Account
                    </Button>
                    <AccountDialog
                        isOpenDialog={isOpen}
                        setIsOpenDialog={setIsOpen}
                        mode={'Add'}
                        onSubmit={onSubmit}
                        isSubmitting={isSubmitting}
                        form={form}
                    />
                </div>
            </div>
            <div
                className={
                    'bg-background h-full border border-#d9d9d9 p-6 pt-8'
                }>
                {error ? (
                    <ErrorUI error={error} reset={mutate} />
                ) : isLoading ? (
                    <Loader2 className={'animate-spin'} />
                ) : (
                    <DataTable
                        columns={userAccountsColumns}
                        data={accounts ?? []}
                        searchProperty={'username'}
                        searchInputPlaceholder={'Search User Name'}
                    />
                )}
            </div>
        </div>
    );
}

export default Accounts;
