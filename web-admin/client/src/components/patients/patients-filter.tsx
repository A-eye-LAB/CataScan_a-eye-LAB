'use client';

import { useState } from 'react';
import { DateRange } from 'react-day-picker';
import { MixerHorizontalIcon } from '@radix-ui/react-icons';
import { PopoverClose } from '@radix-ui/react-popover';
import { useSession } from 'next-auth/react';
import useAdminInstitutions from '@/hooks/api/use-admin-institutions';
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
import { MultiSelect } from '@/components/ui/multi-select';
import dateUtil from '@/lib/utils/date';

function PatientsFilter({
    filters,
    onApply,
}: {
    filters: ApiRequests.GetPatientList;
    onApply: (newFilters: ApiRequests.GetPatientList) => void;
}) {
    const role = useSession().data?.user?.role;
    const { institutions } = useAdminInstitutions(role === 'admin');

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

    const institutionOptions = institutions
        ? institutions.map((inst) => ({
              label: inst.institutionName,
              value: inst.institutionName,
          }))
        : [];

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
                                            ? dateUtil.formatToLocalYYYYMMDD(
                                                  newDate.from
                                              )
                                            : undefined,
                                        dateOfBirthTo: newDate.to
                                            ? dateUtil.formatToLocalYYYYMMDD(
                                                  newDate.to
                                              )
                                            : undefined,
                                    }));
                                }}
                            />
                        </div>

                        {role === 'admin' && (
                            <div className="flex flex-col gap-y-1.5">
                                <span>Institutions</span>
                                <MultiSelect
                                    options={institutionOptions}
                                    value={localFilters.institution || []}
                                    onChange={(selectedInstitutionNames) => {
                                        setLocalFilters((prev) => ({
                                            ...prev,
                                            institution:
                                                selectedInstitutionNames,
                                        }));
                                    }}
                                    placeholder="Select Institutions"
                                />
                            </div>
                        )}
                    </div>

                    <div className="flex justify-end gap-x-2">
                        <PopoverClose asChild>
                            <Button variant="outline">Cancel</Button>
                        </PopoverClose>

                        <PopoverClose asChild>
                            <Button
                                variant="catascan"
                                onClick={() => {
                                    onApply(localFilters);
                                }}>
                                Apply
                            </Button>
                        </PopoverClose>
                    </div>
                </div>
            </PopoverContent>
        </Popover>
    );
}

export default PatientsFilter;
