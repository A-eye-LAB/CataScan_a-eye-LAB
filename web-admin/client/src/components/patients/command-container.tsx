'use client';

import React, { startTransition, useState } from 'react';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import PatientInformationDialog from '@/components/patients/patient-information-dialog';
import useDialog from '@/hooks/use-dialog';
import { usePatientManagement } from '@/hooks/use-patient-management';
import { usePatientFilter } from '@/context/patient-filter-context';
import { Button } from '@/components/ui/button';
import apiManager from '@/lib/api/apiManager';
import appUtil from '@/lib/utils/app';
import { PatientDetail, patientDetailSchema } from '@/lib/types/schema';
import { DEFAULT_VALUES } from '@/lib/constants';

function CommandContainer() {
    const { isOpen, setIsOpen } = useDialog(false);
    const { filters } = usePatientFilter();

    const patientForm = useForm<PatientDetail>({
        resolver: zodResolver(patientDetailSchema),
        defaultValues: {
            ...DEFAULT_VALUES.patientDetail,
            dateOfBirth: new Date(),
        },
    });
    const { isSubmitting, createPatient } = usePatientManagement({
        onCreateSuccess: () => {
            setIsOpen(false);
            patientForm.reset();
            startTransition(() => {
                window.location.reload();
            });
        },
    });

    const [isLoading, setIsLoading] = useState(false);

    const handleCreate = async () => {
        await createPatient(patientForm.getValues());
    };

    const onDownloadCSV = async () => {
        setIsLoading(true);
        try {
            const result = await apiManager.downloadPatientsCSV(filters);

            if (result) {
                appUtil.downloadCSV(result.data);
                toast('Success');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={'flex gap-x-2'}>
            <div>
                <Button
                    variant={'outline'}
                    onClick={onDownloadCSV}
                    disabled={isLoading}>
                    {isLoading ? (
                        <>
                            <Loader2 className={'animate-spin'} />
                            Please wait
                        </>
                    ) : (
                        <>Download CSV</>
                    )}
                </Button>
            </div>
            <div>
                <Button variant={'catascan'} onClick={() => setIsOpen(true)}>
                    Add Patient Profile
                </Button>
                <PatientInformationDialog
                    mode="Add"
                    form={patientForm}
                    onSubmit={handleCreate}
                    isSubmitting={isSubmitting}
                    isOpenDialog={isOpen}
                    setIsOpenDialog={setIsOpen}
                />
            </div>
        </div>
    );
}

export default CommandContainer;
