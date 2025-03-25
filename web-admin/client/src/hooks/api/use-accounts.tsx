import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useAccounts() {
    const params = {};

    const requestUrl = apiUrl.getAdminUserListUrl();

    const { data, isLoading, error, mutate } = useSWR<ApiResponses.GetUserList>(
        [requestUrl, params],
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
