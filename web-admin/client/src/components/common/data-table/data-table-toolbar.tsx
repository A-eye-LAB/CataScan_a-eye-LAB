'use client';

import { ReactElement } from 'react';
import { Table } from '@tanstack/react-table';
import { TStringColumnKeys } from '@/components/common/data-table/index';
import { Input } from '@/components/ui/input';

interface DataTableToolbarProps<
    TData,
    TSearchProperty extends TStringColumnKeys<TData> = TStringColumnKeys<TData>,
> {
    table: Table<TData>;
    searchProperty?: TSearchProperty;
    searchInputPlaceholder?: string;
    filterComponent?: ReactElement;
    showToolbarTotal?: boolean;
    inputClassName?: string;
}

export function DataTableToolbar<
    TData,
    TSearchProperty extends TStringColumnKeys<TData> = TStringColumnKeys<TData>,
>(props: DataTableToolbarProps<TData, TSearchProperty>) {
    const {
        table,
        searchProperty,
        searchInputPlaceholder,
        filterComponent: FilterComponent,
        showToolbarTotal = true,
        inputClassName = 'w-[150px] lg:w-[250px]',
    } = props;

    return (
        <div className="flex items-center justify-between">
            <div className="flex flex-1 items-center gap-x-1.5">
                {showToolbarTotal && (
                    <div>
                        Total{' '}
                        <span
                            className={
                                'font-black text-CATASCAN-text-basic-blue'
                            }>
                            {table.getRowCount()}
                        </span>
                    </div>
                )}
                {searchProperty && (
                    <Input
                        placeholder={searchInputPlaceholder ?? ''}
                        value={
                            (table
                                .getColumn(searchProperty as string)
                                ?.getFilterValue() as string) ?? ''
                        }
                        onChange={(event) =>
                            table
                                .getColumn(searchProperty as string)
                                ?.setFilterValue(event.target.value)
                        }
                        className={`h-8 ${inputClassName}`}
                    />
                )}

                {FilterComponent && FilterComponent}
            </div>
        </div>
    );
}
