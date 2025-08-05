import { ColumnDef } from '@tanstack/react-table';
import { DataTableColumnHeader } from '@/components/common/data-table/data-table-column-header';
import PatientProfilesRowActions from '@/components/common/data-table/data-table-row-actions/patient-profiles-row-actions';
import { Checkbox } from '@/components/ui/checkbox';
import { RegisteredPatient } from '@/lib/types/schema';

const patientProfilesColumns: ColumnDef<RegisteredPatient>[] = [
    {
        id: 'select',
        header: ({ table }) => (
            <Checkbox
                checked={
                    table.getIsAllPageRowsSelected() ||
                    (table.getIsSomePageRowsSelected() && 'indeterminate')
                }
                onCheckedChange={(value) =>
                    table.toggleAllPageRowsSelected(!!value)
                }
                aria-label="Select all"
                className="translate-y-[2px]"
            />
        ),
        cell: ({ row }) => (
            <div className={'flex items-center'}>
                <Checkbox
                    checked={row.getIsSelected()}
                    onCheckedChange={(value) => row.toggleSelected(!!value)}
                    aria-label="Select row"
                    className="translate-y-[2px] "
                    onClick={(event) => {
                        event.stopPropagation();
                    }}
                />
            </div>
        ),
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: 'patientName',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Patient Name" />
        ),
        cell: ({ row }) => <div>{row.getValue('patientName')}</div>,
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
                <PatientProfilesRowActions
                    patientId={row.original.patientId}
                    institutionId={row.original.institutionId}
                    registrationDate={row.original.registrationDate}
                />
            );
        },
    },
];

export default patientProfilesColumns;
