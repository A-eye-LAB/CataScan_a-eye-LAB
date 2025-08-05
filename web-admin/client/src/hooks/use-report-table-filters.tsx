import { useState } from 'react';
import { z } from 'zod';
import { AiResult, sexSchema } from '@/lib/types/schema';

export type FilterState = {
    sex: z.infer<typeof sexSchema> | 'all';
    status: AiResult | 'all';
};

function useReportTableFilters() {
    const [filters, setFilters] = useState<FilterState>({
        sex: 'all',
        status: 'all',
    });

    const [tempFilters, setTempFilters] = useState<FilterState>(filters);

    const updateTempFilters = (key: keyof FilterState, value: string) => {
        setTempFilters((prev) => ({
            ...prev,
            [key]: value,
        }));
    };

    const applyFilters = () => {
        setFilters(tempFilters);
    };

    const resetTempFilters = () => {
        setTempFilters(filters);
    };

    return {
        filters,
        tempFilters,
        setFilters,
        updateTempFilters,
        applyFilters,
        resetTempFilters,
    };
}

export default useReportTableFilters;
