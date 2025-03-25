import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function usePatients(props: ApiRequests.GetPatientList) {
    const params = { ...props };

    const requestUrl = apiUrl.getPatientsUrl();

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetPatientsList>([requestUrl, params], {
            fetcher,
        });

    return {
        patients: data,
        isLoading,
        error,
        mutate,
    };
}

export default usePatients;
