import { ColumnDef } from '@tanstack/react-table';
import { DataTableColumnHeader } from '@/components/common/data-table/data-table-column-header';
import ReportPatientProfileActions from '@/components/common/data-table/data-table-row-actions/report-patient-profile-actions';
import { RegisteredPatient } from '@/lib/types/schema';

const reportsPatientProfilesColumns = (
    reportId: string
): ColumnDef<RegisteredPatient>[] => [
    {
        accessorKey: 'name',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Patient Name" />
        ),
        cell: ({ row }) => (
            <div className="h-[32px] flex items-center max-w-[293px]">
                {row.getValue('name')}
            </div>
        ),
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'sex',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Sex" />
        ),
        cell: ({ row }) => (
            <span className="max-w-[293px]">{row.getValue('sex')}</span>
        ),
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'dateOfBirth',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Date of Birth" />
        ),
        cell: ({ row }) => (
            <span className="max-w-[293px]">{row.getValue('dateOfBirth')}</span>
        ),
        filterFn: (row, id, value) => value.includes(row.getValue(id)),
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'phoneNum',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Phone Number" />
        ),
        cell: ({ row }) => (
            <span className="max-w-[293px]">{row.getValue('phoneNum')}</span>
        ),
        filterFn: (row, id, value) => value.includes(row.getValue(id)),
        enableSorting: true,
        enableHiding: false,
    },
    {
        id: 'actions',
        cell: ({ row }) => {
            return (
                <ReportPatientProfileActions row={row} reportId={reportId} />
            );
        },
    },
];

export default reportsPatientProfilesColumns;
