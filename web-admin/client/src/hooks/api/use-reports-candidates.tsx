import useSWR from 'swr';
import { FilterState } from '@/hooks/use-report-table-filters';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useReportsCandidates(reportId: string, filters: Partial<FilterState>) {
    const params = { ...filters };

    if (params.status === 'all') {
        delete params.status;
    }
    if (params.sex === 'all') {
        delete params.sex;
    }

    const requestUrl = apiUrl.getReportsReportIdCandidatesListUrl(reportId);

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetReportCandidatePatients>([requestUrl, params], {
            fetcher,
        });

    return {
        patients: data,
        isLoading,
        error,
        mutate,
    };
}

export default useReportsCandidates;
