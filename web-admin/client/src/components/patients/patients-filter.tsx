'use client';

import { useState } from 'react';
import { DateRange } from 'react-day-picker';
import { MixerHorizontalIcon } from '@radix-ui/react-icons';
import { PopoverClose } from '@radix-ui/react-popover';
import { Button } from '@/components/ui/button';
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from '@/components/ui/popover';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { DatePickerWithRange } from '@/components/ui/datepicker-with-range';

function PatientsFilter({
    filters,
    onApply,
}: {
    filters: ApiRequests.GetPatientList;
    onApply: (newFilters: ApiRequests.GetPatientList) => void;
}) {
    const [localFilters, setLocalFilters] =
        useState<ApiRequests.GetPatientList>({
            ...filters,
        });

    const [date, setDate] = useState<DateRange>({
        from: localFilters.dateOfBirthFrom
            ? new Date(localFilters.dateOfBirthFrom)
            : undefined,
        to: localFilters.dateOfBirthTo
            ? new Date(localFilters.dateOfBirthTo)
            : undefined,
    });

    return (
        <Popover>
            <PopoverTrigger asChild>
                <Button variant="outline">
                    <MixerHorizontalIcon />
                    <span>Filters</span>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[298px] p-6" align={'start'}>
                <div className="flex flex-col gap-y-6">
                    <div className="flex flex-col gap-y-4">
                        <div className="flex flex-col gap-y-1.5">
                            <span className="text-sm">Sex</span>
                            <Select
                                value={localFilters.sex || 'all'}
                                onValueChange={(
                                    value: 'male' | 'female' | 'all'
                                ) => {
                                    setLocalFilters((prev) => ({
                                        ...prev,
                                        sex:
                                            value === 'all' ? undefined : value, // 'all' 선택 시 undefined
                                    }));
                                }}>
                                <SelectTrigger className="w-[243px]">
                                    <SelectValue placeholder="All" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="all">All</SelectItem>
                                    <SelectItem value="male">Male</SelectItem>
                                    <SelectItem value="female">
                                        Female
                                    </SelectItem>
                                    <SelectItem value="other">Other</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div className="flex flex-col gap-y-1.5">
                            <span>Date of Birth</span>
                            <DatePickerWithRange
                                buttonWidth={243}
                                date={date}
                                setDate={(newDate) => {
                                    setDate(newDate);
                                    setLocalFilters((prev) => ({
                                        ...prev,
                                        dateOfBirthFrom: newDate.from
                                            ?.toISOString()
                                            .split('T')[0],
                                        dateOfBirthTo: newDate.to
                                            ?.toISOString()
                                            .split('T')[0],
                                    }));
                                }}
                            />
                        </div>
                    </div>

                    <div className="flex justify-end gap-x-2">
                        <PopoverClose asChild>
                            <Button variant="outline">Cancel</Button>
                        </PopoverClose>

                        <Button
                            variant="catascan"
                            onClick={() => {
                                onApply(localFilters);
                            }}>
                            Apply
                        </Button>
                    </div>
                </div>
            </PopoverContent>
        </Popover>
    );
}

export default PatientsFilter;
