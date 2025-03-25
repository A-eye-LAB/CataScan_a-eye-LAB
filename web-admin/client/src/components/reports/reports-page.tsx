'use client';

import { useState } from 'react';
import { DateRange } from 'react-day-picker';
import { useRouter } from 'next/navigation';
import { addDays } from 'date-fns';
import { Row } from '@tanstack/react-table';
import ReportsFilter from '@/components/reports/reports-filter';
import useReports from '@/hooks/api/use-reports';
import useReportTableFilters from '@/hooks/use-report-table-filters';
import { reportsColumns } from '@/components/common/data-table/columns';
import ErrorUI from '@/components/common/error';
import DataTable from '@/components/common/data-table';
import { DatePickerWithRange } from '@/components/ui/datepicker-with-range';
import dateUtil from '@/lib/utils/date';
import { Report } from '@/lib/types/schema';

const BEFORE_DAYS = 90;

function ReportsPage() {
    const router = useRouter();
    const [date, setDate] = useState<DateRange>({
        from: addDays(new Date(), -BEFORE_DAYS),
        to: new Date(),
    });

    const {
        filters,
        tempFilters,
        updateTempFilters,
        applyFilters,
        resetTempFilters,
    } = useReportTableFilters();

    const { reports, isLoading, error, mutate } = useReports({
        startDate: date.from ? dateUtil.formatToYYYYMMDD(date.from) : date.from,
        endDate: date.to ? dateUtil.formatToYYYYMMDD(date.to) : date.to,
        // sortBy: 'scanDate',
        // sortDir: 'asc',
        ...filters,
    });

    const rowClassName = (row: Row<Report>) => {
        if (row.getValue('linkStatus')) {
            return 'opacity-30';
        }
        return '';
    };

    if (error) {
        return <ErrorUI error={error} reset={mutate} />;
    }

    return (
        <div>
            <div className={'mb-6 w-[270px]'}>
                <DatePickerWithRange date={date} setDate={setDate} />
            </div>
            <div
                className={
                    'bg-background h-full border border-#d9d9d9 p-6 pt-8'
                }>
                {isLoading || !reports ? (
                    <></>
                ) : (
                    <DataTable
                        columns={reportsColumns}
                        data={reports}
                        searchProperty={'patientName'}
                        searchInputPlaceholder={'Search Patient Name'}
                        inputClassName={'flex-1'}
                        emptyText={'No Report'}
                        rowClassName={rowClassName}
                        onClickRow={(row) => {
                            router.push(`/reports/${row.original.reportId}`);
                        }}
                        filterComponent={
                            <ReportsFilter
                                tempFilters={tempFilters}
                                updateTempFilters={updateTempFilters}
                                applyFilters={applyFilters}
                                resetTempFilters={resetTempFilters}
                            />
                        }
                    />
                )}
            </div>
        </div>
    );
}

export default ReportsPage;
