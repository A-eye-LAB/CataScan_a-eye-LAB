'use client';

import React, { createContext, useContext, useState } from 'react';

type PatientFilterContextType = {
    filters: ApiRequests.GetPatientList;
    setFilters: (filters: ApiRequests.GetPatientList) => void;
};

const PatientFilterContext = createContext<
    PatientFilterContextType | undefined
>(undefined);

export const PatientFilterProvider = ({
    children,
}: {
    children: React.ReactNode;
}) => {
    const [filters, setFilters] = useState<ApiRequests.GetPatientList>({});

    return (
        <PatientFilterContext.Provider value={{ filters, setFilters }}>
            {children}
        </PatientFilterContext.Provider>
    );
};

export const usePatientFilter = () => {
    const context = useContext(PatientFilterContext);
    if (context === undefined) {
        throw new Error(
            'usePatientFilter must be used within a PatientFilterProvider'
        );
    }
    return context;
};
