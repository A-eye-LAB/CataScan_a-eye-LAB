'use client';

import React, { startTransition, useEffect, useState } from 'react';
import { toast } from 'sonner';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import PatientInformationDialog from '@/components/patients/patient-information-dialog';
import usePatient from '@/hooks/api/use-patient';
import { usePatientManagement } from '@/hooks/use-patient-management';
import { usePatientFilter } from '@/context/patient-filter-context';
import DeleteDialog from '@/components/common/alert-dialog/delete-dialog';
import ModifyActionRow from '@/components/common/data-table/data-table-row-actions/modify-action-dropdown-menu';
import { PatientDetail, patientDetailSchema } from '@/lib/types/schema';
import { DEFAULT_VALUES, PATIENT } from '@/lib/constants';

type TPatientProfilesRowActionsProps = {
    patientId: number;
};

function PatientProfilesRowActions(props: TPatientProfilesRowActionsProps) {
    const { patientId } = props;

    const { filters } = usePatientFilter();

    const [isOpenEditDialog, setIsOpenEditDialog] = useState(false);
    const [isOpenDeleteDialog, setIsOpenDeleteDialog] = useState(false);

    const { patient, mutate } = usePatient(`${patientId}`, isOpenEditDialog);

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
                await mutate();
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
        if (patient?.registrationDate) {
            await updatePatient(
                patientId,
                patientForm.getValues(),
                patient.registrationDate
            );
        } else {
            toast('Error: patient.registrationDate is undefined');
        }
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

    return (
        <>
            <ModifyActionRow
                setIsOpenEditDialog={setIsOpenEditDialog}
                setIsOpenDeleteDialog={setIsOpenDeleteDialog}
                isEditRender={
                    filters?.dataStatus !== PATIENT.IS_DELETED_PROFILES_CHECKED
                }
            />

            <DeleteDialog
                isOpenDialog={isOpenDeleteDialog}
                setIsOpenDialog={setIsOpenDeleteDialog}
                onDelete={handleDelete}
                title={'Delete Patient Profile'}
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

export default PatientProfilesRowActions;
