'use client';

import { usePathname } from 'next/navigation';
import CommandContainer from '@/components/patients/command-container';
import { PatientFilterProvider } from '@/context/patient-filter-context';

type TPatientLayoutProps = {
    children: React.ReactNode;
    detail: React.ReactNode;
};

function PatientLayout(props: TPatientLayoutProps) {
    const { children, detail } = props;
    const pathname = usePathname();

    return (
        <PatientFilterProvider>
            <div>
                <div className={'flex justify-between mb-6'}>
                    <h1 className={'text-3xl font-bold'}>Patient Profiles</h1>
                    <CommandContainer />
                </div>

                <div className={'flex gap-x-4'}>
                    <div className={pathname ? 'flex-1' : 'flex-grow'}>
                        {children}
                    </div>
                    {pathname !== '/patients' && (
                        <div className={'flex-1 mt-7'}>{detail}</div>
                    )}
                </div>
            </div>
        </PatientFilterProvider>
    );
}

export default PatientLayout;
