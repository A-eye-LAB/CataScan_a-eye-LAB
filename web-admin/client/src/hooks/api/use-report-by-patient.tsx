import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';
import useRoleBasedUrl from './use-role-based-url';

function useReportByPatient(
    patientId: number,
    institutionId: string,
    props: ApiRequests.GetReportByPatient
) {
    const params = { ...props };

    const requestUrl = useRoleBasedUrl({
        adminUrl:
            apiUrl.getAdminInstitutionInstitutionIdPatientsPatientIdReportsUrl(
                institutionId,
                patientId
            ),
        userUrl: apiUrl.getReportsPatientPatientIdUrl(patientId),
    });

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetReportByPatient>([requestUrl, params], {
            fetcher,
        });

    return {
        reports: data,
        isLoading,
        error,
        mutate,
    };
}

export default useReportByPatient;
