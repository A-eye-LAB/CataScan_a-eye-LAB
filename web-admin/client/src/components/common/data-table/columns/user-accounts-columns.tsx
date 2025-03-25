import { ColumnDef } from '@tanstack/react-table';
import { DataTableColumnHeader } from '@/components/common/data-table/data-table-column-header';
import DataTableRowActions from '@/components/common/data-table/data-table-row-actions/user-account-row-actions';
import { RegisteredUserAccount } from '@/lib/types/schema';

const userAccountsColumns: ColumnDef<RegisteredUserAccount>[] = [
    {
        accessorKey: 'username',
        size: 293,
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="User Name" />
        ),
        cell: ({ row }) => <div>{row.getValue('username')}</div>,
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'institutionName',
        size: 293,
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Institution" />
        ),
        cell: ({ row }) => <div>{row.getValue('institutionName')}</div>,
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'role',
        size: 293,

        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Role" />
        ),
        cell: ({ row }) => {
            return <div>{row.getValue('role')}</div>;
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'createdDate',
        size: 293,

        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Created Date" />
        ),
        cell: ({ row }) => {
            return <div>{row.getValue('createdDate')}</div>;
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'updatedDate',
        size: 293,

        header: ({ column }) => (
            <DataTableColumnHeader column={column} title="Updated Date" />
        ),
        cell: ({ row }) => {
            return <div>{row.getValue('updatedDate')}</div>;
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id));
        },
        enableSorting: true,
        enableHiding: false,
    },
    {
        id: 'actions',
        size: 50,
        cell: ({ row }) => {
            const userId = row.original.id;
            return <DataTableRowActions userId={userId} />;
        },
    },
];

export default userAccountsColumns;
