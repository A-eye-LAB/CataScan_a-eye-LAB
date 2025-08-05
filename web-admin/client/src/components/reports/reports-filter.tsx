import { MixerHorizontalIcon } from '@radix-ui/react-icons';
import { PopoverClose } from '@radix-ui/react-popover';
import { FilterState } from '@/hooks/use-report-table-filters';
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

type TReportsFilterProps = {
    tempFilters: FilterState;
    updateTempFilters: (key: keyof FilterState, value: string) => void;
    applyFilters: () => void;
    resetTempFilters: () => void;
};

function ReportsFilter(props: TReportsFilterProps) {
    const { tempFilters, updateTempFilters, applyFilters, resetTempFilters } =
        props;

    return (
        <Popover>
            <PopoverTrigger asChild>
                <Button variant="outline">
                    <MixerHorizontalIcon />
                    <span>Filters</span>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-[318px] p-6" align={'start'}>
                <div className={'flex flex-col gap-y-6'}>
                    <div className={'flex flex-col gap-y-1.5'}>
                        <span className={'text-sm font-medium'}>Sex</span>
                        <Select
                            value={tempFilters.sex}
                            onValueChange={(value) =>
                                updateTempFilters('sex', value)
                            }>
                            <SelectTrigger>
                                <SelectValue placeholder="All" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All</SelectItem>
                                <SelectItem value="male">Male</SelectItem>
                                <SelectItem value="female">Female</SelectItem>
                                <SelectItem value="other">Other</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div className={'flex flex-col gap-y-1.5'}>
                        <span className={'text-sm font-medium'}>Status</span>
                        <Select
                            value={tempFilters.status}
                            onValueChange={(value) =>
                                updateTempFilters('status', value)
                            }>
                            <SelectTrigger>
                                <SelectValue placeholder="All" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All</SelectItem>
                                <SelectItem value="lowRisk">Normal</SelectItem>
                                <SelectItem value="requiresAttention">
                                    Requires Attention
                                </SelectItem>
                            </SelectContent>
                        </Select>
                    </div>
                    <div className={'flex justify-end gap-x-2'}>
                        <PopoverClose asChild>
                            <Button
                                variant={'outline'}
                                onClick={resetTempFilters}>
                                Cancel
                            </Button>
                        </PopoverClose>
                        <PopoverClose asChild>
                            <Button variant={'catascan'} onClick={applyFilters}>
                                Apply
                            </Button>
                        </PopoverClose>
                    </div>
                </div>
            </PopoverContent>
        </Popover>
    );
}

export default ReportsFilter;
