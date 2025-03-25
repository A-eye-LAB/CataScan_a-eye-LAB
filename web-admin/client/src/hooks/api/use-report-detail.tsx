import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useReportDetail(reportId: string, shouldFetch = true) {
    const requestUrl = apiUrl.getReportsReportIdUrl(reportId);

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetReportsById>(
            reportId && shouldFetch ? [requestUrl] : null,
            {
                fetcher,
            }
        );

    return {
        report: data,
        isLoading,
        error,
        mutate,
    };
}

export default useReportDetail;
