import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useAccountData(id: number | undefined, shouldFetch = true) {
    const params = {};

    const requestUrl = apiUrl.getAdminUserIdUrl(id!);

    const { data, isLoading, error, mutate } = useSWR<ApiResponses.GetUserData>(
        shouldFetch ? [requestUrl, params] : null,
        {
            fetcher,
        }
    );

    return {
        account: data,
        isLoading,
        error,
        mutate,
    };
}

export default useAccountData;
