import useUserRole from '@/hooks/auth/use-user-role';

type UrlOptions = {
    adminUrl: string;
    userUrl: string;
};

function useRoleBasedUrl({ adminUrl, userUrl }: UrlOptions): string | null {
    const userRole = useUserRole();

    if (userRole === 'admin') {
        return adminUrl;
    } else if (userRole) {
        return userUrl;
    }

    return null;
}

export default useRoleBasedUrl;
