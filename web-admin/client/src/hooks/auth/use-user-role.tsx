import { useSession } from 'next-auth/react';

function useUserRole() {
    const { data: session } = useSession();
    const user = session?.user;

    return user?.role;
}

export default useUserRole;
