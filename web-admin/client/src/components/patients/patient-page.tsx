'use client';

import { useRouter } from 'next/navigation';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import PatientsFilter from '@/components/patients/patients-filter';
import usePatients from '@/hooks/api/use-patients';
import useDialog from '@/hooks/use-dialog';
import { usePatientFilter } from '@/context/patient-filter-context';
import { patientProfilesColumns } from '@/components/common/data-table/columns';
import ErrorUI from '@/components/common/error';
import BaseAlertDialog from '@/components/common/alert-dialog/base-alert-dialog';
import DataTable from '@/components/common/data-table';
import { Checkbox } from '@/components/ui/checkbox';
import { Button } from '@/components/ui/button';
import { PATIENT } from '@/lib/constants';
import apiManager from '@/lib/api/apiManager';

function PatientsPage() {
    const { filters, setFilters } = usePatientFilter();

    const { patients, isLoading, error, mutate } = usePatients({
        ...filters,
    });

    const { isOpen, setIsOpen, toggle } = useDialog(false);

    const router = useRouter();

    const restorePatients = async (patientIds: number[]) => {
        try {
            const restoreRequests = patientIds.map((id) =>
                apiManager.restorePatient(id).then(
                    (res) => ({ id, status: 'fulfilled', res }),
                    (err) => ({ id, status: 'rejected', err })
                )
            );

            const results = await Promise.allSettled(restoreRequests);
            const failed = results.filter((r) => r.status === 'rejected');

            if (failed.length > 0) {
                toast(`${failed.length} patients failed to restore`);
            } else {
                toast('All patients restored successfully!');
            }

            await mutate();
            toggle();
        } catch (error) {
            toast('Error: Check console for details');
            console.error(error);
        }
    };

    if (isLoading) {
        return <Loader2 className={'animate-spin'} />;
    }

    if (error) {
        return <ErrorUI error={error} reset={mutate} />;
    }

    return (
        <div>
            <div className={'mt-6 mb-1 flex justify-end'}>
                <div className={'flex items-center gap-x-2.5'}>
                    <Checkbox
                        checked={filters?.dataStatus === 0}
                        onCheckedChange={(checked) => {
                            setFilters({
                                ...filters,
                                dataStatus: checked ? 0 : 1,
                            });
                        }}
                    />
                    <span>Deleted Profiles</span>
                </div>
            </div>
            <div
                className={
                    'bg-background h-full border border-#d9d9d9 p-6 pt-8'
                }>
                <DataTable
                    columns={patientProfilesColumns}
                    data={patients ?? []}
                    searchProperty={'patientName'}
                    searchInputPlaceholder={'Search...'}
                    filterComponent={
                        <PatientsFilter
                            filters={filters}
                            onApply={(newFilters) => {
                                setFilters({ ...newFilters });
                            }}
                        />
                    }
                    onClickRow={(row) => {
                        router.push(
                            `/patients/${row.original.patientId}?institutionId=${row.original.institutionId}`
                        );
                    }}
                    renderSelectedRowActions={(table) => {
                        if (
                            filters?.dataStatus ===
                            PATIENT.IS_DELETED_PROFILES_CHECKED
                        ) {
                            const selectedRows = table
                                .getSelectedRowModel()
                                .rows.map((row) => row.original);
                            const count = selectedRows.length;

                            return (
                                <>
                                    <div className="absolute top-[-10px]">
                                        <Button
                                            variant={'catascan'}
                                            onClick={toggle}
                                            disabled={count < 1}>
                                            Restore Selected
                                        </Button>
                                    </div>
                                    <BaseAlertDialog
                                        isOpenDialog={isOpen}
                                        setIsOpenDialog={setIsOpen}
                                        title={'Restore Patient Profile?'}
                                        onConfirm={(event) => {
                                            event.preventDefault();

                                            restorePatients(
                                                selectedRows.map(
                                                    (row) => row.patientId
                                                )
                                            );
                                        }}
                                        onCancel={toggle}
                                        confirmButtonText="Restore"
                                        description={
                                            <span
                                                className={
                                                    'whitespace-break-spaces text-CATASCAN-text-basic-foreground'
                                                }>
                                                <span className="font-bold text-CATASCAN-text-basic-blue">{`${count} `}</span>
                                                {`patient profile${count > 1 ? 's' : ''} will be restored and immediately available for viewing and editing.`}
                                            </span>
                                        }
                                    />
                                </>
                            );
                        }
                        return <></>;
                    }}
                    emptyElement={
                        <div className="h-[664px] flex items-center justify-center">
                            No Patient
                        </div>
                    }
                />
            </div>
        </div>
    );
}

export default PatientsPage;
