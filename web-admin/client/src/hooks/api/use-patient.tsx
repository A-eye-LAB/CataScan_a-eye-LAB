import useSWR from 'swr';
import { Session } from 'next-auth';
import useUserRole from '@/hooks/auth/use-user-role';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function usePatient(
    patientId?: string,
    institutionId?: string,
    shouldFetch = true
) {
    const params = {};

    const userRole = useUserRole();

    const getRequestUrl = (role: Session['user']['role'] | undefined) => {
        if (role === 'admin') {
            return apiUrl.getAdminInstitutionInstitutionIdPatientsPatientIdUrl(
                institutionId!,
                patientId!
            );
        } else if (role) {
            return apiUrl.getPatientsPatientIdUrl(patientId!);
        }
        return null;
    };

    const requestUrl = getRequestUrl(userRole);

    const { data, isLoading, error, mutate } = useSWR<ApiResponses.GetPatient>(
        shouldFetch && requestUrl ? [requestUrl, params] : null,
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
