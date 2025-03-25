import useSWR from 'swr';
import { FilterState } from '@/hooks/use-report-table-filters';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

type TUserReportsProps = {
    startDate: string | undefined;
    endDate: string | undefined;
} & Partial<
    Pick<ApiRequests.GetReportListFromDateRange, 'sortBy' | 'sortDir'>
> &
    Partial<FilterState>;

function useReports(props: TUserReportsProps) {
    const { startDate, endDate } = props;
    const params = { ...props };

    if (params.status === 'all') {
        delete params.status;
    }

    if (params.sex === 'all') {
        delete params.sex;
    }

    const requestUrl = apiUrl.getReportsUrl();

    const { data, isLoading, error, mutate } = useSWR<
        ApiResponses.GetReportListFromDateRange[]
    >(startDate && endDate ? [requestUrl, params] : null, {
        fetcher,
    });

    let reports = undefined;

    if (data) {
        reports = data.map((report) => {
            const aiResults = [report.leftAiResult, report.rightAiResult];
            let aiResult: 'requiresAttention' | 'lowRisk' | 'unknown';

            if (aiResults.includes('requiresAttention')) {
                aiResult = 'requiresAttention';
            } else if (aiResults.includes('lowRisk')) {
                aiResult = 'lowRisk';
            } else {
                aiResult = 'unknown';
            }

            return {
                ...report,
                aiResult,
            };
        });
    }

    return {
        reports,
        isLoading,
        error,
        mutate,
    };
}

export default useReports;
