import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function usePatientHealthInfo(patientId: number) {
    const params = {};

    const requestUrl = apiUrl.getPatientsPatientIdProfileUrl(patientId);

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetPatientProfiles>([requestUrl, params], {
            fetcher,
        });

    return {
        healthInfo: data?.healthInfo,
        isLoading,
        error,
        mutate,
    };
}

export default usePatientHealthInfo;
