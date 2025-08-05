'use client';

import { useRouter } from 'next/navigation';
import { Row } from '@tanstack/react-table';
import ReportsFilter from '@/components/reports/reports-filter';
import useReports from '@/hooks/api/use-reports';
import useReportTableFilters from '@/hooks/use-report-table-filters';
import { useDateContext } from '@/context/date-context';
import { reportsColumns } from '@/components/common/data-table/columns';
import ErrorUI from '@/components/common/error';
import DataTable from '@/components/common/data-table';
import dateUtil from '@/lib/utils/date';
import { Report } from '@/lib/types/schema';

function ReportsPage() {
    const router = useRouter();
    const { date } = useDateContext();

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
                        emptyElement={
                            <div className="h-[664px] flex items-center justify-center">
                                No Report
                            </div>
                        }
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
