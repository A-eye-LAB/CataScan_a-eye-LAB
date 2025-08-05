import useSWR from 'swr';
import useUserRole from '@/hooks/auth/use-user-role';
import useRoleBasedUrl from '@/hooks/api/use-role-based-url';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function usePatients(props: ApiRequests.GetPatientList) {
    const userRole = useUserRole();

    const requestUrl = useRoleBasedUrl({
        adminUrl: apiUrl.getAdminPatientsUrl(),
        userUrl: apiUrl.getPatientsUrl(),
    });

    const { institution, ...otherProps } = props;
    const params: Omit<ApiRequests.GetPatientList, 'institution'> & {
        institution?: string[];
    } = { ...otherProps };

    if (userRole === 'admin' && institution) {
        params.institution = institution;
    }

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetPatientsList | null>(
            requestUrl ? [requestUrl, params] : null,
            {
                fetcher,
            }
        );

    return {
        patients: data,
        isLoading,
        error,
        mutate,
    };
}

export default usePatients;
