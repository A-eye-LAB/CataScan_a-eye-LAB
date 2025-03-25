'use client';

import { Loader2 } from 'lucide-react';
import PatientsDataTable from '@/components/patients/patients-data-table';
import PatientsFilter from '@/components/patients/patients-filter';
import usePatients from '@/hooks/api/use-patients';
import { usePatientFilter } from '@/context/patient-filter-context';
import { patientProfilesColumns } from '@/components/common/data-table/columns';
import ErrorUI from '@/components/common/error';
import { Checkbox } from '@/components/ui/checkbox';

function PatientsPage() {
    const { filters, setFilters } = usePatientFilter();

    const { patients, isLoading, error, mutate } = usePatients(filters);

    if (isLoading) {
        return <Loader2 className={'animate-spin'} />;
    }

    if (error || !patients) {
        return <ErrorUI error={error} reset={mutate} />;
    }

    return (
        <div>
            <div className={'mb-1 flex justify-end'}>
                <div className={'flex items-center gap-x-2.5'}>
                    <Checkbox
                        className={
                            'data-[state=checked]:bg-CATASCAN-button data-[state=checked]:border-none'
                        }
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
                <PatientsDataTable
                    columns={patientProfilesColumns}
                    data={patients}
                    searchProperty={'name'}
                    searchInputPlaceholder={'Search...'}
                    filterComponent={
                        <PatientsFilter
                            filters={filters}
                            onApply={(newFilters) => {
                                setFilters({ ...newFilters });
                            }}
                        />
                    }
                />
            </div>
        </div>
    );
}

export default PatientsPage;
