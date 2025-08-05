import { useFormContext } from 'react-hook-form';
import { CalendarIcon } from 'lucide-react';
import { format } from 'date-fns';
import {
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from '@/components/ui/popover';
import { Calendar } from '@/components/ui/calendar';
import { Button } from '@/components/ui/button';
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select';
import { PatientDetail } from '@/lib/types/schema';

function BasicTab() {
    const form = useFormContext<PatientDetail>();

    return (
        <div className={'grid grid-cols-2 gap-x-4 gap-y-10 '}>
            <FormField
                name={'patientName'}
                control={form.control}
                render={({ field }) => (
                    <FormItem>
                        <FormLabel
                            className={'text-CATASCAN-text-basic-foreground'}>
                            Profile Name *
                        </FormLabel>
                        <FormControl>
                            <Input id={'patientName'} {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )}
            />
            <FormField
                name={'sex'}
                control={form.control}
                render={({ field }) => {
                    return (
                        <FormItem>
                            <FormLabel
                                className={
                                    'text-CATASCAN-text-basic-foreground'
                                }>
                                Sex *
                            </FormLabel>
                            <Select
                                onValueChange={field.onChange}
                                defaultValue={field.value}
                                value={field.value}>
                                <FormControl>
                                    <SelectTrigger>
                                        <SelectValue
                                            placeholder={'Select sex'}
                                        />
                                    </SelectTrigger>
                                </FormControl>

                                <SelectContent>
                                    <SelectItem value="male">Male</SelectItem>
                                    <SelectItem value="female">
                                        Female
                                    </SelectItem>
                                    <SelectItem value="other">Other</SelectItem>
                                </SelectContent>
                            </Select>
                        </FormItem>
                    );
                }}
            />
            <FormField
                name={'dateOfBirth'}
                control={form.control}
                render={({ field }) => {
                    return (
                        <FormItem>
                            <FormLabel>Date of Birth</FormLabel>
                            <Popover>
                                <PopoverTrigger asChild>
                                    <FormControl>
                                        <Button
                                            id="date"
                                            variant={'outline'}
                                            className={`w-full justify-start ${!field.value && 'text-muted-foreground'}`}>
                                            <CalendarIcon />
                                            {field.value ? (
                                                format(
                                                    new Date(field.value),
                                                    'yyyy-MM-dd'
                                                )
                                            ) : (
                                                <span>Pick a date</span>
                                            )}
                                        </Button>
                                    </FormControl>
                                </PopoverTrigger>

                                <PopoverContent
                                    className="w-auto p-0 pointer-events-auto"
                                    align="start">
                                    <Calendar
                                        mode="single"
                                        selected={field.value}
                                        onSelect={(date) => {
                                            if (date) {
                                                const utcDate = new Date(
                                                    Date.UTC(
                                                        date.getFullYear(),
                                                        date.getMonth(),
                                                        date.getDate()
                                                    )
                                                );

                                                field.onChange(utcDate);
                                            }
                                        }}
                                        defaultMonth={field.value}
                                        captionLayout="dropdown"
                                        initialFocus={true}
                                    />
                                </PopoverContent>
                            </Popover>
                            <FormMessage />
                        </FormItem>
                    );
                }}
            />

            <FormField
                name={'phoneNum'}
                control={form.control}
                render={({ field }) => (
                    <FormItem>
                        <FormLabel
                            className={'text-CATASCAN-text-basic-foreground'}>
                            Phone Number
                        </FormLabel>
                        <FormControl>
                            <Input id={'phoneNum'} {...field} />
                        </FormControl>
                        <FormMessage />
                    </FormItem>
                )}
            />
        </div>
    );
}

export default BasicTab;
