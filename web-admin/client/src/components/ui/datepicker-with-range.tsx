'use client';
import * as React from 'react';
import { format } from 'date-fns';
import { Calendar as CalendarIcon } from 'lucide-react';
import { DateRange } from 'react-day-picker';

import { cn } from '@/lib/utils/tailwind';
import { Button } from '@/components/ui/button';
import { Calendar } from '@/components/ui/calendar';
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from '@/components/ui/popover';

interface IDatePickerWithRangeProps {
    className?: React.HTMLAttributes<HTMLDivElement>;
    buttonWidth?: number;
    date: DateRange;
    setDate: (date: DateRange) => void;
}

export function DatePickerWithRange(props: IDatePickerWithRangeProps) {
    const { className, buttonWidth = 300, date, setDate } = props;

    return (
        <div className={cn('grid gap-2', className)}>
            <Popover>
                <PopoverTrigger asChild>
                    <Button
                        id="date"
                        variant={'outline'}
                        className={cn(
                            `w-[${buttonWidth}px] justify-start text-left font-normal`,
                            !date && 'text-muted-foreground'
                        )}>
                        <CalendarIcon />
                        {date?.from ? (
                            date.to ? (
                                <>
                                    {format(date.from, 'yyyy-MM-dd')} -{' '}
                                    {format(date.to, 'yyyy-MM-dd')}
                                </>
                            ) : (
                                format(date.from, 'yyyy-MM-dd')
                            )
                        ) : (
                            <span>Pick a date</span>
                        )}
                    </Button>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                    <Calendar
                        initialFocus
                        mode="range"
                        defaultMonth={date?.from}
                        selected={date}
                        onSelect={(selected) => {
                            if (selected) {
                                setDate(selected);
                            } else {
                                setDate(date);
                            }
                        }}
                        numberOfMonths={2}
                        captionLayout="dropdown"
                    />
                </PopoverContent>
            </Popover>
        </div>
    );
}
