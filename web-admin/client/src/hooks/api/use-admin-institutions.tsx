import useSWR from 'swr';
import fetcher from '@/lib/api/fetcher';
import apiUrl from '@/lib/api/apiUrl';

function useAdminInstitutions(shouldFetch: boolean = true) {
    const requestUrl = apiUrl.getAdminInsitutionsUrl();

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetAdminInstitutionList>(
            shouldFetch && requestUrl ? [requestUrl] : null,
            {
                fetcher,
            }
        );

    return {
        institutions: data,
        isLoading,
        error,
        mutate,
    };
}

export default useAdminInstitutions;
