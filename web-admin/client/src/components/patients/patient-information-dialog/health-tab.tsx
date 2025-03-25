import { Path, UseFormReturn } from 'react-hook-form';
import {
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { Checkbox } from '@/components/ui/checkbox';
import { Input } from '@/components/ui/input';
import { HealthInfo, PatientDetail } from '@/lib/types/schema';

const RELATED_INFO = [
    {
        id: 'cataract',
        label: 'Cataract',
    },
    {
        id: 'diabetes',
        label: 'Diabetes',
    },
    {
        id: 'hypertension',
        label: 'Hypertension',
    },
    {
        id: 'dontKnow',
        label: "Don't Know",
    },
] as const;

const GENERAL_INFO = [
    {
        name: 'systolicBp',
        label: 'Systolic Blood Pressure',
        type: 'number',
        id: 'systolicBp',
    },
    {
        name: 'diastolicBp',
        label: 'Diastolic Blood Pressure',
        type: 'number',
        id: 'diastolicBp',
    },
    {
        name: 'rightEyeVision',
        label: 'Vision R/E',
        type: 'number',
        id: 'rightEyeVision',
    },
    {
        name: 'leftEyeVision',
        label: 'Vision L/E',
        type: 'number',
        id: 'leftEyeVision',
    },
    {
        name: 'bloodSugarLevel',
        label: 'Blood Sugar Level (mg/dL)',
        type: 'number',
        id: 'bloodSugarLevel',
    },
    {
        name: 'remarks',
        label: 'Remark',
        type: 'text',
        id: 'remark',
    },
] as const;

type FormData = HealthInfo | PatientDetail;

type THealthTabProps<T extends FormData> = {
    form: UseFormReturn<T>;
    fieldNamePrefix?: T extends PatientDetail ? 'healthInfo' : never;
};

function HealthTab<T extends FormData>(props: THealthTabProps<T>) {
    const { form, fieldNamePrefix } = props;

    const getFieldName = <K extends keyof HealthInfo>(name: K): Path<T> => {
        if (fieldNamePrefix) {
            return `${fieldNamePrefix}.${name}` as Path<T>;
        }
        return name as unknown as Path<T>;
    };

    return (
        <div className={'flex flex-col gap-y-10 '}>
            <div>
                <div className="mb-4 flex flex-col">
                    <span className="text-base">Related Info.</span>
                    <span className="text-[0.8rem] text-muted-foreground">
                        Select the items you want to display in the sidebar.
                    </span>
                </div>

                <ul className={'grid grid-flow-col'}>
                    {RELATED_INFO.map((info) => (
                        <li key={info.id}>
                            <FormField
                                control={form.control}
                                name={getFieldName(info.id as keyof HealthInfo)}
                                render={({ field }) => {
                                    return (
                                        <FormItem
                                            key={info.id}
                                            className="flex flex-row items-start space-x-3 space-y-0">
                                            <FormControl>
                                                <Checkbox
                                                    className={
                                                        'data-[state=checked]:bg-CATASCAN-button data-[state=checked]:border-none'
                                                    }
                                                    checked={Boolean(
                                                        field.value
                                                    )}
                                                    onCheckedChange={
                                                        field.onChange
                                                    }
                                                />
                                            </FormControl>
                                            <FormMessage />
                                            <FormLabel className="font-normal">
                                                {info.label}
                                            </FormLabel>
                                        </FormItem>
                                    );
                                }}
                            />
                        </li>
                    ))}
                </ul>
            </div>
            <ul
                className={
                    'w-full grid grid-flow-row gap-x-4 gap-y-10 grid-cols-2 '
                }>
                {GENERAL_INFO.map(({ name, label, type }) => (
                    <li key={name}>
                        <FormField
                            name={getFieldName(name as keyof HealthInfo)}
                            control={form.control}
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel className="text-CATASCAN-text-basic-foreground">
                                        {label}
                                    </FormLabel>
                                    <FormControl>
                                        <Input
                                            className={
                                                name === 'remarks' ? 'h-20' : ''
                                            }
                                            type={type}
                                            value={
                                                field.value?.toString() ?? ''
                                            }
                                            onChange={(e) => {
                                                const value =
                                                    type === 'number'
                                                        ? Number(
                                                              e.target.value
                                                          ) || null
                                                        : e.target.value;
                                                field.onChange(value);
                                            }}
                                            onBlur={field.onBlur}
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default HealthTab;
