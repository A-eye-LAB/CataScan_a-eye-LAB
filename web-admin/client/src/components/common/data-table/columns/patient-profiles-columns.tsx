import { ColumnDef } from '@tanstack/react-table';
import { DataTableColumnHeader } from '@/components/common/data-table/data-table-column-header';
import PatientProfilesRowActions from '@/components/common/data-table/data-table-row-actions/patient-profiles-row-actions';
import { RegisteredPatient } from '@/lib/types/schema';

const patientProfilesColumns: ColumnDef<RegisteredPatient>[] = [
    {
        accessorKey: 'name',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Patient Name" />
        ),
        cell: ({ row }) => <div>{row.getValue('name')}</div>,
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'sex',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Sex" />
        ),
        cell: ({ row }) => {
            return <span>{row.getValue('sex')}</span>;
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'age',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="age" />
        ),
        cell: ({ row }) => {
            return <span>{row.getValue('age') ?? '-'}</span>;
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'dateOfBirth',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Date of Birth" />
        ),
        cell: ({ row }) => {
            return <span>{row.getValue('dateOfBirth')}</span>;
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },

    {
        accessorKey: 'phoneNum',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Phone Number" />
        ),
        cell: ({ row }) => {
            return <span>{row.getValue('phoneNum')}</span>;
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        id: 'actions',
        cell: ({ row }) => {
            return (
                <PatientProfilesRowActions patientId={row.original.patientId} />
            );
        },
    },
];

export default patientProfilesColumns;
