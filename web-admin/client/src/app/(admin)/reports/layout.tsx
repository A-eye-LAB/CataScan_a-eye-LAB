'use client';
import { usePathname } from 'next/navigation';
import DownloadImagesDialog from '@/components/reports/download-images-dialog';
import useUserRole from '@/hooks/auth/use-user-role';
import { useDateContext, DateProvider } from '@/context/date-context';
import { DatePickerWithRange } from '@/components/ui/datepicker-with-range';

type TPatientLayoutProps = {
    children: React.ReactNode;
    detail: React.ReactNode;
};

function ReportsLayoutContent(props: TPatientLayoutProps) {
    const { children, detail } = props;

    const pathname = usePathname();
    const { date, setDate } = useDateContext();
    const userRole = useUserRole();

    return (
        <div>
            <div className={'sticky top-[70px] z-10'}>
                <h1 className={'text-3xl font-bold mb-[10px]'}>Reports</h1>
                <div className={'flex justify-between'}>
                    <DatePickerWithRange date={date} setDate={setDate} />
                    {userRole === 'admin' && <DownloadImagesDialog />}
                </div>
            </div>
            <div className={'flex gap-x-4 mt-12'}>
                <div className={pathname ? 'flex-[6]' : 'flex-grow'}>
                    {children}
                </div>
                {pathname !== '/reports' && (
                    <div
                        className={'flex-[9]'}
                        // style={{ marginTop: 'calc(36px + 1.5rem)' }}
                    >
                        {detail}
                    </div>
                )}
            </div>
        </div>
    );
}

function ReportsLayout(props: TPatientLayoutProps) {
    return (
        <DateProvider>
            <ReportsLayoutContent {...props} />
        </DateProvider>
    );
}

export default ReportsLayout;
