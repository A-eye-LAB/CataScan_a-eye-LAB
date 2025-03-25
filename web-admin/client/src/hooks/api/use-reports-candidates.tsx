import useSWR from 'swr';
import { FilterState } from '@/hooks/use-report-table-filters';
import apiUrl from '@/lib/api/apiUrl';
import fetcher from '@/lib/api/fetcher';

function useReportsCandidates(reportId: string, filters: Partial<FilterState>) {
    const params = { ...filters };

    // TODO: need check params type
    if (params.status === 'all') {
        delete params.status;
    } else if (params.status === 'lowRisk') {
        params.status = 'EyeStatus.normal';
    } else if (params.status === 'requiresAttention') {
        params.status = 'EyeStatus.abnormal';
    }

    const requestUrl = apiUrl.getReportsReportIdCandidatesListUrl(reportId);

    const { data, isLoading, error, mutate } =
        useSWR<ApiResponses.GetReportCandidatePatients>([requestUrl, params], {
            fetcher,
        });

    return {
        patients: data?.data,
        isLoading,
        error,
        mutate,
    };
}

export default useReportsCandidates;
