'use client';
import { usePathname } from 'next/navigation';

type TPatientLayoutProps = {
    children: React.ReactNode;
    detail: React.ReactNode;
};

function ReportsLayout(props: TPatientLayoutProps) {
    const { children, detail } = props;
    const pathname = usePathname();

    return (
        <div>
            <div className={'mb-2.5'}>
                <h1 className={'text-3xl font-bold'}>Reports</h1>
            </div>

            <div className={'flex gap-x-4'}>
                <div className={pathname ? 'flex-[6]' : 'flex-grow'}>
                    {children}
                </div>
                {pathname !== '/reports' && (
                    <div
                        className={'flex-[9]'}
                        style={{ marginTop: 'calc(36px + 1.5rem)' }}>
                        {detail}
                    </div>
                )}
            </div>
        </div>
    );
}

export default ReportsLayout;
