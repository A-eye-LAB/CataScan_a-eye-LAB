import { useState } from 'react';
import { toast } from 'sonner';
import { PatientDetail } from '@/lib/types/schema';
import apiManager from '@/lib/api/apiManager';
import { validateStatus } from '@/lib/api/restClient';

type SuccessCallbacks = {
    onCreateSuccess?: () => void;
    onUpdateSuccess?: () => void;
    onDeleteSuccess?: () => void;
};

export const usePatientManagement = (callbacks?: SuccessCallbacks) => {
    const [isSubmitting, setIsSubmitting] = useState(false);

    const createPatient = async (data: PatientDetail) => {
        setIsSubmitting(true);
        try {
            const patientData = data;
            const basicInfo = {
                name: patientData.name,
                sex: patientData.sex,
                phoneNum: patientData.phoneNum,
                registrationDate: new Date(),
                dateOfBirth: patientData.dateOfBirth,
                remarks: patientData.healthInfo.remarks || '',
            };
            const healthInfo = {
                ...patientData.healthInfo,
            };

            delete healthInfo.remarks;

            const requestData = {
                ...basicInfo,
                healthInfo,
            };

            const result = await apiManager.createPatient(requestData);

            if (validateStatus(result.status)) {
                toast('Success');
                callbacks?.onCreateSuccess?.();
            } else {
                toast('Error: Check console for details');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsSubmitting(false);
        }
    };

    const updatePatient = async (
        patientId: number,
        data: PatientDetail,
        registrationDate: string
    ) => {
        setIsSubmitting(true);
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const { healthInfo, ...newData } = data;

        try {
            const result = await apiManager.updatePatient(patientId, {
                ...newData,
                registrationDate: new Date(registrationDate),
            });

            if (validateStatus(result.status)) {
                toast('Success');
                callbacks?.onUpdateSuccess?.();
            } else {
                toast('Error: Check console for details');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsSubmitting(false);
        }
    };

    const deletePatient = async (patientId: number) => {
        try {
            const result = await apiManager.deletePatient(patientId);

            if (validateStatus(result.status)) {
                toast('Success');
                callbacks?.onDeleteSuccess?.();
            } else {
                toast('Error: Check console for details');
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        }
    };

    return {
        isSubmitting,
        createPatient,
        updatePatient,
        deletePatient,
    } as const;
};
