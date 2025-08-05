import useSWR from 'swr';
import { Session } from 'next-auth';
import useUserRole from '@/hooks/auth/use-user-role';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function usePatientHealthInfo(patientId: number, institutionId?: string) {
    const params = {};

    const userRole = useUserRole();

    const getRequestUrl = (role: Session['user']['role'] | undefined) => {
        if (role === 'admin') {
            return apiUrl.getAdminInstitutionInstitutionIdPatientsPatientIdProfileUrl(
                institutionId!,
                `${patientId}`
            );
        } else if (role) {
            return apiUrl.getPatientsPatientIdProfileUrl(patientId);
        }
        return null;
    };

    const requestUrl = getRequestUrl(userRole);

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
