import Image from 'next/image';
import { ColumnDef } from '@tanstack/react-table';
import { DataTableColumnHeader } from '@/components/common/data-table/data-table-column-header';
import PatientReportRowActions from '@/components/common/data-table/data-table-row-actions/patient-report-row-actions';
import { ReportDetail } from '@/lib/types/schema';
import renderUtil from '@/lib/utils/renderUtils';
import dateUtil from '@/lib/utils/date';

const confirmationRenderer = (data: string) => {
    return `/confirm-group/${data}.png`;
};

const patientReportsColumns: ColumnDef<ReportDetail>[] = [
    {
        accessorKey: 'reportId',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Report ID" />
        ),
        cell: ({ row }) => (
            <span className="max-w-[100px]">{row.getValue('reportId')}</span>
        ),
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'scanDate',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Scan Date" />
        ),
        cell: ({ row }) => {
            return (
                <span className="max-w-[202px]">
                    {dateUtil.formatUTCToLocalString(row.getValue('scanDate'))}
                </span>
            );
        },
        enableSorting: true,
        enableHiding: false,
    },

    {
        id: 'leftAiResult',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Left Eye" />
        ),
        cell: ({ row }) => {
            return (
                <div className="min-w-[140px]">
                    {renderUtil.renderAiResultBadge(
                        row.getValue('leftAiResult'),
                        'rounded-full'
                    )}
                </div>
            );
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },

    {
        id: 'rightAiResult',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Right Eye" />
        ),
        cell: ({ row }) => {
            return (
                <div className="min-w-[140px]">
                    {renderUtil.renderAiResultBadge(
                        row.getValue('rightAiResult'),
                        'rounded-full'
                    )}
                </div>
            );
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        id: 'diagnoses',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Confirmation(L/R)" />
        ),
        cell: ({ row }) => {
            return (
                <div className="max-w-[128px]">
                    <div className={'flex gap-x-1 justify-center'}>
                        {row.original.leftEyeDiagnosis && (
                            <Image
                                width={16}
                                height={16}
                                src={confirmationRenderer(
                                    row.original.leftEyeDiagnosis
                                )}
                                alt={row.original.leftEyeDiagnosis}
                            />
                        )}
                        {row.original.rightEyeDiagnosis && (
                            <Image
                                width={16}
                                height={16}
                                src={confirmationRenderer(
                                    row.original.rightEyeDiagnosis
                                )}
                                alt={row.original.rightEyeDiagnosis}
                            />
                        )}
                    </div>
                </div>
            );
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
            return <PatientReportRowActions row={row} />;
        },
    },
];

export default patientReportsColumns;
