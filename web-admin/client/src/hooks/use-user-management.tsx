import { useState } from 'react';
import { toast } from 'sonner';
import { UserAccountForm } from '@/lib/types/schema';
import apiManager from '@/lib/api/apiManager';
import { validateStatus } from '@/lib/api/restClient';

const useUserManagement = (onSuccess?: () => void) => {
    const [isSubmitting, setIsSubmitting] = useState(false);

    const createUser = async (data: UserAccountForm) => {
        setIsSubmitting(true);
        try {
            const result = await apiManager.createUser({
                username: data.username,
                institutionName: data.institutionName,
                password: data.password,
            });

            if (validateStatus(result.status)) {
                toast('Success');
                onSuccess?.();
            } else {
                toast('Error: Check console for details');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsSubmitting(false);
        }
    };

    const updateUser = async (userId: number, data: UserAccountForm) => {
        setIsSubmitting(true);
        try {
            const result = await apiManager.updateUserData({
                id: userId,
                username: data.username,
                institutionName: data.institutionName,
                password: data.password,
            });

            if (validateStatus(result.status)) {
                toast('Success');
                onSuccess?.();
            } else {
                toast('Error: Check console for details');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsSubmitting(false);
        }
    };

    const deleteUser = async (userId: number) => {
        try {
            const result = await apiManager.deleteUser({ id: userId });

            if (validateStatus(result.status)) {
                toast('Success');
                onSuccess?.();
            } else {
                toast('Error: Check console for details');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        }
    };

    return {
        isSubmitting,
        createUser,
        updateUser,
        deleteUser,
    };
};

export default useUserManagement;
