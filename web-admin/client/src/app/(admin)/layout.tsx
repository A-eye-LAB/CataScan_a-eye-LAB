import { getServerSession } from 'next-auth';
import { redirect } from 'next/navigation';
import AppSidebar from '@/components/common/app-sidebar';
import LogoutWrapper from '@/components/common/logout-wrapper';
import { SidebarProvider } from '@/components/ui/sidebar';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import TriangleDownIcon from '@/components/ui/icon/TriangleDown';
import { Toaster } from '@/components/ui/sonner';
import { authOptions } from '@/lib/utils/authOptions';

type AdminLayoutProps = {
    children: React.ReactNode;
};

async function AdminLayout(props: AdminLayoutProps) {
    const { children } = props;
    const session = await getServerSession(authOptions);

    if (!session?.user?.token) {
        redirect('/login');
    }

    return (
        <SidebarProvider>
            <AppSidebar />
            <div className={'w-full'}>
                <header
                    className={
                        'flex justify-end h-15 pr-6 py-3.5 gap-x-2 bg-CATASCAN-background-baseline border-b-2 sticky top-0'
                    }>
                    <Avatar
                        className={
                            'p-2 border border-CATASCAN-border-basic-split'
                        }>
                        <AvatarImage
                            src={'/profile.png'}
                            alt={session?.user?.name || 'profile image'}
                        />
                        <AvatarFallback />
                    </Avatar>
                    <DropdownMenu>
                        <DropdownMenuTrigger className={'text-xs font-bold'}>
                            <div className={'flex gap-x-2'}>
                                <span
                                    className={
                                        'text-CATASCAN-text-basic-secondary'
                                    }>
                                    {session?.user?.name}
                                </span>
                                <TriangleDownIcon />
                            </div>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent>
                            <DropdownMenuItem
                                className={
                                    'flex justify-center font-medium hover:cursor-pointer'
                                }>
                                <LogoutWrapper>Logout</LogoutWrapper>
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                </header>
                <main
                    className={
                        'bg-CATASCAN-background-baseline h-full pl-6 pt-8 pr-9 pb-6'
                    }>
                    {children}
                </main>
                <Toaster />
            </div>
        </SidebarProvider>
    );
}

export default AdminLayout;
