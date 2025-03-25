import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function usePatient(patientId?: string, shouldFetch = true) {
    const params = {};

    const requestUrl = apiUrl.getPatientsPatientIdUrl(patientId!);

    const { data, isLoading, error, mutate } = useSWR<ApiResponses.GetPatient>(
        shouldFetch ? [requestUrl, params] : null,
        {
            fetcher,
        }
    );

    return {
        patient: data,
        isLoading,
        error,
        mutate,
    };
}

export default usePatient;
