'use client';
import { createContext, useContext, useState, ReactNode } from 'react';
import { DateRange } from 'react-day-picker';
import { addDays } from 'date-fns';

const BEFORE_DAYS = 90;

interface DateContextType {
    date: DateRange;
    setDate: (date: DateRange) => void;
}

const DateContext = createContext<DateContextType | undefined>(undefined);

export function DateProvider({ children }: { children: ReactNode }) {
    const [date, setDate] = useState<DateRange>({
        from: addDays(new Date(), -BEFORE_DAYS),
        to: new Date(),
    });

    return (
        <DateContext.Provider value={{ date, setDate }}>
            {children}
        </DateContext.Provider>
    );
}

export function useDateContext() {
    const context = useContext(DateContext);
    if (context === undefined) {
        throw new Error('useDateContext must be used within a DateProvider');
    }
    return context;
}
