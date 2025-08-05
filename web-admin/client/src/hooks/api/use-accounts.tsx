import useSWR from 'swr';
import useUserRole from '@/hooks/auth/use-user-role';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useAccounts() {
    const params = {};

    const userRole = useUserRole();
    const requestUrl = apiUrl.getAdminUsersUrl();

    const { data, isLoading, error, mutate } = useSWR<ApiResponses.GetUserList>(
        userRole === 'admin' ? [requestUrl, params] : null,
        {
            fetcher,
        }
    );

    return {
        accounts: data,
        isLoading,
        error,
        mutate,
    };
}

export default useAccounts;
