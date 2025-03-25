'use client';

import { useRouter } from 'next/navigation';
import DataTable, { DataTableProps } from '@/components/common/data-table';
import { RegisteredPatient } from '@/lib/types/schema';

type TValue = RegisteredPatient[keyof RegisteredPatient];
type TSearchProperty = {
    [K in keyof RegisteredPatient]: RegisteredPatient[K] extends string
        ? K
        : never;
}[keyof RegisteredPatient];

type TRegisteredPatientsDataTableProps = DataTableProps<
    RegisteredPatient,
    TValue,
    TSearchProperty
>;

function PatientsDataTable(props: TRegisteredPatientsDataTableProps) {
    const {
        columns,
        data,
        searchProperty,
        searchInputPlaceholder,
        filterComponent,
    } = props;

    const router = useRouter();

    return (
        <DataTable
            columns={columns}
            data={data}
            searchProperty={searchProperty}
            searchInputPlaceholder={searchInputPlaceholder}
            filterComponent={filterComponent}
            onClickRow={(row) => {
                router.push(`/patients/${row.original.patientId}`);
            }}
        />
    );
}

export default PatientsDataTable;
