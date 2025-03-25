import React, { useState } from 'react';
import { Check, ChevronDown } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandItem,
} from '@/components/ui/command';
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from '@/components/ui/popover';
import { cn } from '@/lib/utils/tailwind';

type TMultiSelectProps = {
    options: { label: string; value: string }[];
    placeholder?: string;
    emptyMessage?: string;
    value: string[];
    onChange: (value: string[]) => void;
    className?: string;
};

const MultiSelect = (props: TMultiSelectProps) => {
    const {
        options,
        placeholder = 'Select options...',
        emptyMessage = 'No option found.',
        value,
        onChange,
        className,
    } = props;

    const [open, setOpen] = useState(false);

    const handleSelect = (selectedValue: string) => {
        if (value.includes(selectedValue)) {
            onChange(value.filter((item) => item !== selectedValue));
        } else {
            onChange([...value, selectedValue]);
        }
    };

    const placeholderText = () => {
        if (value.length < 1) {
            return placeholder;
        }
        if (value.length === 1) {
            const target = options.find((v) => v.value === value[0]);

            return target?.label ?? value;
        }
        return `${value.length} Selected`;
    };

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    role="combobox"
                    aria-expanded={open}
                    className={cn('w-full justify-between', className)}>
                    <span className={'truncate'}>{placeholderText()}</span>
                    <ChevronDown className="h-4 w-4 opacity-50" />
                </Button>
            </PopoverTrigger>
            <PopoverContent className="p-0">
                <Command>
                    <CommandEmpty>{emptyMessage}</CommandEmpty>
                    <CommandGroup>
                        {options.map((option) => (
                            <CommandItem
                                key={option.value}
                                onSelect={() => handleSelect(option.value)}>
                                <Check
                                    className={cn(
                                        'mr-2 h-4 w-4',
                                        value.includes(option.value)
                                            ? 'opacity-100'
                                            : 'opacity-0'
                                    )}
                                />
                                {option.label}
                            </CommandItem>
                        ))}
                    </CommandGroup>
                </Command>
            </PopoverContent>
        </Popover>
    );
};

export { MultiSelect };
