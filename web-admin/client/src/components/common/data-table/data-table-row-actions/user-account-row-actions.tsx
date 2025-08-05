'use client';

import React, { startTransition, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import AccountDialog from '@/components/accounts/account-dialog';
import useAccountData from '@/hooks/api/use-account-data';
import useUserManagement from '@/hooks/use-user-management';
import BaseAlertDialog from '@/components/common/alert-dialog/base-alert-dialog';
import ModifyActionRow from '@/components/common/data-table/data-table-row-actions/modify-action-dropdown-menu';
import { DEFAULT_VALUES } from '@/lib/constants';
import { UserAccountForm, userAccountFormSchema } from '@/lib/types/schema';

type TUserAccountRowActionsProps = {
    userId: number;
};

function UserAccountRowActions(props: TUserAccountRowActionsProps) {
    const { userId } = props;

    const [isOpenEditDialog, setIsOpenEditDialog] = useState(false);

    const { account } = useAccountData(userId, isOpenEditDialog);

    const form = useForm<UserAccountForm>({
        resolver: zodResolver(userAccountFormSchema),
        defaultValues: DEFAULT_VALUES.account,
    });

    const { isSubmitting, updateUser, deleteUser } = useUserManagement(
        async () => {
            form.reset();
            setIsOpenEditDialog(false);
            setIsOpenDeleteDialog(false);
            startTransition(() => {
                window.location.reload();
            });
        }
    );

    const [isOpenDeleteDialog, setIsOpenDeleteDialog] = useState(false);

    const handleUpdate = async (data: UserAccountForm) => {
        await updateUser(userId, data);
    };

    const handleDelete = async () => {
        await deleteUser(userId);
    };

    useEffect(() => {
        if (account) {
            form.reset({ ...account });
        }
    }, [account, form]);

    return (
        <>
            <ModifyActionRow
                setIsOpenEditDialog={setIsOpenEditDialog}
                setIsOpenDeleteDialog={setIsOpenDeleteDialog}
            />

            <BaseAlertDialog
                isOpenDialog={isOpenDeleteDialog}
                setIsOpenDialog={setIsOpenDeleteDialog}
                onConfirm={handleDelete}
                onCancel={() => {
                    setIsOpenDeleteDialog(false);
                }}
                title={'Delete User Account'}
                confirmButtonText="Delete"
                description={
                    'This action cannot be undone.\nDo you want to proceed?'
                }
            />
            <AccountDialog
                mode={'Edit'}
                isOpenDialog={isOpenEditDialog}
                setIsOpenDialog={setIsOpenEditDialog}
                onSubmit={handleUpdate}
                form={form}
                isSubmitting={isSubmitting}
            />
        </>
    );
}

export default UserAccountRowActions;
