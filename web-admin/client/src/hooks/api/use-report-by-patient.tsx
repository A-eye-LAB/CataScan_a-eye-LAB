import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useReportByPatient(
    patientId: number,
    props: ApiRequests.GetReportByPatient
) {
    const params = { ...props };

    const requestUrl = apiUrl.getReportsPatientPatientIdUrl(patientId);

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetReportByPatient>([requestUrl, params], {
            fetcher,
        });

    return {
        reports: data?.data,
        isLoading,
        error,
        mutate,
    };
}

export default useReportByPatient;
