'use client';

import { ReactElement, useState } from 'react';
import {
    ColumnDef,
    ColumnFiltersState,
    flexRender,
    getCoreRowModel,
    getFacetedRowModel,
    getFacetedUniqueValues,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    Row,
    SortingState,
    useReactTable,
    VisibilityState,
} from '@tanstack/react-table';
import { Table as TableType } from '@tanstack/react-table';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { DataTablePagination } from '@/components/common/data-table/data-table-pagination';
import { DataTableToolbar } from '@/components/common/data-table/data-table-toolbar';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table';

export type TStringColumnKeys<TData> = {
    [K in keyof TData]: TData[K] extends string ? K : never;
}[keyof TData];

export interface DataTableProps<
    TData,
    TValue,
    TSearchProperty extends TStringColumnKeys<TData> = TStringColumnKeys<TData>,
> {
    columns: ColumnDef<TData, TValue>[];
    data: TData[];
    searchProperty?: TSearchProperty;
    searchInputPlaceholder?: string;
    filterComponent?: ReactElement;
    onClickRow?: (row: Row<TData>) => void;
    rowClassName?: (row: Row<TData>) => string;
    showToolbarTotal?: boolean;
    showPagination?: boolean;
    inputClassName?: string;
    emptyElement?: string | ReactElement;
    renderExpandedRow?: (row: Row<TData>) => ReactElement;
    renderSelectedRowActions?: (table: TableType<TData>) => ReactElement;
}

function DataTable<TData, TValue>({
    columns,
    data,
    searchProperty,
    searchInputPlaceholder,
    filterComponent: FilterComponent,
    onClickRow,
    rowClassName,
    showToolbarTotal,
    showPagination = true,
    inputClassName,
    emptyElement = 'No results',
    renderExpandedRow,
    renderSelectedRowActions,
}: DataTableProps<TData, TValue>) {
    const [rowSelection, setRowSelection] = useState({});
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>(
        {}
    );
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
    const [sorting, setSorting] = useState<SortingState>([]);
    const [expandedRows, setExpandedRows] = useState<Record<string, boolean>>(
        {}
    );

    const table = useReactTable({
        data,
        columns,
        state: {
            sorting,
            columnVisibility,
            rowSelection,
            columnFilters,
        },
        enableRowSelection: true,
        onRowSelectionChange: setRowSelection,
        onSortingChange: setSorting,
        onColumnFiltersChange: setColumnFilters,
        onColumnVisibilityChange: setColumnVisibility,
        getCoreRowModel: getCoreRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFacetedRowModel: getFacetedRowModel(),
        getFacetedUniqueValues: getFacetedUniqueValues(),
    });

    return (
        <div>
            <div className="mb-5">
                <DataTableToolbar
                    table={table}
                    searchProperty={searchProperty}
                    searchInputPlaceholder={searchInputPlaceholder}
                    filterComponent={FilterComponent}
                    showToolbarTotal={showToolbarTotal}
                    inputClassName={inputClassName}
                />
            </div>
            <div className="rounded-md border mb-6">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => {
                                    return (
                                        <TableHead
                                            key={header.id}
                                            colSpan={header.colSpan}>
                                            {header.isPlaceholder
                                                ? null
                                                : flexRender(
                                                      header.column.columnDef
                                                          .header,
                                                      header.getContext()
                                                  )}
                                        </TableHead>
                                    );
                                })}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody className={table.getRowModel().rows?.length ? '' : 'h-full'}>
                        {table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.flatMap((row) => [
                                <TableRow
                                    key={row.id}
                                    className={
                                        rowClassName
                                            ? rowClassName(row)
                                            : undefined
                                    }
                                    data-state={
                                        row.getIsSelected() && 'selected'
                                    }
                                    onClick={() => {
                                        setExpandedRows((prev) => ({
                                            ...prev,
                                            [row.id]: !prev[row.id],
                                        }));
                                        if (onClickRow) {
                                            onClickRow(row);
                                        }
                                    }}>
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id}>
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                    {renderExpandedRow && (
                                        <TableCell className="cursor-pointer">
                                            {expandedRows[row.id] ? (
                                                <ChevronUp size={16} />
                                            ) : (
                                                <ChevronDown size={16} />
                                            )}
                                        </TableCell>
                                    )}
                                </TableRow>,
                                expandedRows[row.id] && renderExpandedRow ? (
                                    <TableRow key={`${row.id}-expanded`}>
                                        <TableCell
                                            colSpan={columns.length + 1}
                                            className={'p-0'}>
                                            {renderExpandedRow(row)}
                                        </TableCell>
                                    </TableRow>
                                ) : null,
                            ])
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="text-center text-muted-foreground"
                                    >
                                    {emptyElement}
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <div className="relative">
                {renderSelectedRowActions && renderSelectedRowActions(table)}
                {showPagination && <DataTablePagination table={table} />}
            </div>
        </div>
    );
}

export default DataTable;
