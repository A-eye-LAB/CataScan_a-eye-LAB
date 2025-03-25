'use client';

import { startTransition } from 'react';
import { useParams } from 'next/navigation';
import { Loader2 } from 'lucide-react';
import PatientDetailTabsArea from '@/components/patients/patient-detail-tabs-area';
import usePatient from '@/hooks/api/use-patient';
import ErrorUI from '@/components/common/error';
import { Separator } from '@/components/ui/separator';
import dateUtil from '@/lib/utils/date';

function PatientDetail() {
    const params = useParams();
    const { patientId } = params;

    const { patient, isLoading, error } = usePatient(patientId as string);

    const BASIC_INFORMATATIONS = [
        {
            name: 'sex',
            label: 'Sex',
            value: patient?.sex || '-',
        },
        {
            name: 'dateOfBirth',
            label: 'Date of Birth',
            value: patient?.dateOfBirth || '-',
        },
        {
            name: 'age',
            label: 'Age',
            value: patient?.age ?? '-',
        },
        {
            name: 'phoneNumber',
            label: 'Phone Number',
            value: patient?.phoneNum || '-',
        },
    ];

    if (isLoading) {
        return <Loader2 className={'animate-spin'} />;
    }

    if (!patientId || !patient || error) {
        return (
            <ErrorUI
                error={error}
                reset={() => {
                    startTransition(() => {
                        window.location.reload();
                    });
                }}
            />
        );
    }

    return (
        <div
            className={'h-full border bg-background border-[#d9d9d9] p-6 pt-8'}>
            <div className={'flex flex-col gap-y-4'}>
                <div className={'flex flex-col gap-y-6  px-5 py-6'}>
                    <div className={'flex'}>
                        <h1
                            className={
                                'text-CATASCAN-text-basic-heading font-bold text-2xl'
                            }>
                            {patient.name}
                        </h1>
                    </div>
                    <div className={'grid grid-cols-4 gap-x-5'}>
                        {BASIC_INFORMATATIONS.map((info, index) => {
                            return (
                                <div
                                    key={`${info.value}`}
                                    className={
                                        'flex align-middle justify-between'
                                    }>
                                    <div className={'flex flex-col'}>
                                        <span
                                            className={
                                                'text-CATASCAN-text-basic-tertiary font-bold'
                                            }>
                                            {info.label}
                                        </span>
                                        <span
                                            className={
                                                'text-CATASCAN-text-basic-primary font-semibold'
                                            }>
                                            {typeof info.value === 'object'
                                                ? dateUtil.formatToYYYYMMDD(
                                                      info.value
                                                  )
                                                : info.value}
                                        </span>
                                    </div>
                                    {BASIC_INFORMATATIONS.length - 1 ===
                                    index ? (
                                        <></>
                                    ) : (
                                        <Separator
                                            orientation={'vertical'}
                                            className="h-[40px] bg-CATASCAN-border-basic-split"
                                        />
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>
                <PatientDetailTabsArea
                    patientId={parseInt(patientId as string, 0)}
                />
            </div>
        </div>
    );
}

export default PatientDetail;
