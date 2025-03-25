import useSWR from 'swr';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useReportComments(reportId: string) {
    const requestUrl = apiUrl.getReportsReportIdCommentsUrl(reportId);

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetReportComments>([requestUrl], {
            fetcher,
        });

    return {
        comments: data?.comments,
        isLoading,
        error,
        mutate,
    };
}

export default useReportComments;
