'use client';

import React, { startTransition, useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import PatientInformationDialog from '@/components/patients/patient-information-dialog';
import usePatient from '@/hooks/api/use-patient';
import { usePatientManagement } from '@/hooks/use-patient-management';
import { usePatientFilter } from '@/context/patient-filter-context';
import ModifyActionRow from '@/components/common/data-table/data-table-row-actions/modify-action-dropdown-menu';
import BaseAlertDialog from '@/components/common/alert-dialog/base-alert-dialog';
import { PatientDetail, patientDetailSchema } from '@/lib/types/schema';
import { DEFAULT_VALUES, PATIENT } from '@/lib/constants';

type TPatientProfilesRowActionsProps = {
    patientId: number;
    registrationDate: string;
    institutionId?: number;
};

function PatientProfilesRowActions(props: TPatientProfilesRowActionsProps) {
    const { patientId, registrationDate, institutionId } = props;

    const { filters } = usePatientFilter();

    const [isOpenEditDialog, setIsOpenEditDialog] = useState(false);
    const [isOpenDeleteDialog, setIsOpenDeleteDialog] = useState(false);

    const { patient, mutate } = usePatient(
        `${patientId}`,
        institutionId ? `${institutionId}` : undefined,
        isOpenEditDialog
    );

    const patientForm = useForm<PatientDetail>({
        resolver: zodResolver(patientDetailSchema),
        defaultValues: {
            ...DEFAULT_VALUES.patientDetail,
            dateOfBirth: new Date(),
        },
    });

    const { isSubmitting, updatePatient, deletePatient } = usePatientManagement(
        {
            onDeleteSuccess: async () => {
                setIsOpenDeleteDialog(false);
                startTransition(() => {
                    window.location.reload();
                });
            },
            onUpdateSuccess: async () => {
                setIsOpenEditDialog(false);
                patientForm.reset();
                await mutate();
                startTransition(() => {
                    window.location.reload();
                });
            },
        }
    );

    const handleUpdate = async () => {
        await updatePatient(
            patientId,
            patientForm.getValues(),
            registrationDate
        );
    };

    const handleDelete = async () => {
        await deletePatient(patientId);
    };

    useEffect(() => {
        if (patient && isOpenEditDialog) {
            const patientWithDateObject = {
                ...patient,
                dateOfBirth:
                    patient.dateOfBirth instanceof Date
                        ? patient.dateOfBirth
                        : new Date(patient.dateOfBirth),
                healthInfo: {
                    ...DEFAULT_VALUES.healthInfo,
                },
            };
            patientForm.reset(patientWithDateObject);
        }
    }, [isOpenEditDialog, patient, patientForm]);

    if (filters?.dataStatus === PATIENT.IS_DELETED_PROFILES_CHECKED) {
        return <></>;
    } else {
        return (
            <>
                <ModifyActionRow
                    setIsOpenEditDialog={setIsOpenEditDialog}
                    setIsOpenDeleteDialog={setIsOpenDeleteDialog}
                />

                <BaseAlertDialog
                    isOpenDialog={isOpenDeleteDialog}
                    setIsOpenDialog={setIsOpenDeleteDialog}
                    onConfirm={handleDelete}
                    title={'Delete Patient Profile'}
                    confirmButtonText="Delete"
                    description="This action cannot be undone.\nDo you want to proceed?"
                />

                <PatientInformationDialog
                    mode="Edit"
                    form={patientForm}
                    onSubmit={handleUpdate}
                    isSubmitting={isSubmitting}
                    isOpenDialog={isOpenEditDialog}
                    setIsOpenDialog={setIsOpenEditDialog}
                />
            </>
        );
    }
}

export default PatientProfilesRowActions;
