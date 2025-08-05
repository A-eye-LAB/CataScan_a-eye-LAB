import { ColumnDef } from '@tanstack/react-table';
import { Link1Icon, LinkBreak1Icon } from '@radix-ui/react-icons';
import { DataTableColumnHeader } from '@/components/common/data-table/data-table-column-header';
import { Checkbox } from '@/components/ui/checkbox';
import { Report } from '@/lib/types/schema';
import renderUtil from '@/lib/utils/renderUtils';
import dateUtil from '@/lib/utils/date';

const reportsColumns: ColumnDef<Report>[] = [
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
            <div className={'h-[32px] flex items-center'}>
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
        cell: ({ row }) => <span>{row.getValue('patientName')}</span>,
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
                <span>
                    {dateUtil.formatUTCToLocalString(row.getValue('scanDate'))}
                </span>
            );
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'aiResult',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Result" />
        ),
        cell: ({ row }) => {
            return (
                <div className="min-w-[140px]">
                    {renderUtil.renderAiResultBadge(
                        row.getValue('aiResult'),
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
        accessorKey: 'linkStatus',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Status" />
        ),
        cell: ({ row }) => {
            return (
                <span>
                    {row.getValue('linkStatus') ? (
                        <Link1Icon />
                    ) : (
                        <LinkBreak1Icon />
                    )}
                </span>
            );
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },
];

export default reportsColumns;
