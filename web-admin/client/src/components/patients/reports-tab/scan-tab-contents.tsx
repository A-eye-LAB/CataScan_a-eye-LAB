import Image from 'next/image';
import { UseFormReturn } from 'react-hook-form';
import { Separator } from '@/components/ui/separator';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Textarea } from '@/components/ui/textarea';
import {
    FormControl,
    FormField,
    FormItem,
    FormLabel,
} from '@/components/ui/form';
import { ReportDetail } from '@/lib/types/schema';
import appUtil from '@/lib/utils/app';
import renderUtil from '@/lib/utils/renderUtils';

const RADIO_ITEMS = [
    {
        value: 'certainlyNormal',
        label: 'Certainly Normal',
        imageUrl: '/confirm-group/certainly-normal.png',
    },
    {
        value: 'probablyNormal',
        label: 'Probably Normal',
        imageUrl: '/confirm-group/probably-normal.png',
    },
    {
        value: 'uncertain',
        label: 'Uncertain',
        imageUrl: '/confirm-group/uncertain.png',
    },
    {
        value: 'probablyCataract',
        label: 'Probably Cataract',
        imageUrl: '/confirm-group/probably-cataract.png',
    },
    {
        value: 'certainlyCataract',
        label: 'Certainly Cataract',
        imageUrl: '/confirm-group/certainly-cataract.png',
    },
];

type TScanTabContentsProps = {
    form: UseFormReturn<ReportDetail>;
    eye: 'left' | 'right';
};

function ScanTabContents(props: TScanTabContentsProps) {
    const { form, eye } = props;

    const eyeImagePath =
        eye === 'left'
            ? form.getValues().leftEyeImageFilePath
            : form.getValues().rightEyeImageFilePath;

    const ITEMS = [
        {
            text: 'Report ID',
            content: `${form.getValues().reportId}`,
        },
        {
            text: 'Sex',
            content: form.getValues().sex ?? '-',
        },
        {
            text: 'Scan Date',
            content: `${form.getValues().scanDate}`,
        },
        {
            text: 'Result',
            content: appUtil.getEyeStatus(form.getValues()[`${eye}AiResult`]),
        },
    ];

    return (
        <div>
            <Separator className="bg-CATASCAN-border-basic-split my-3" />
            {/*<div className={'flex flex-col'}>*/}
            <div className={'flex flex-col gap-y-2.5'}>
                <span className={'text-lg font-medium'}>AI Result</span>
                <div>
                    <div
                        className={
                            'flex gap-x-5 p-5 bg-CATASCAN-background-baseline rounded-lg'
                        }>
                        {eyeImagePath && (
                            <Image
                                width={200}
                                height={200}
                                className={'rounded-lg'}
                                src={eyeImagePath}
                                alt={'scan image'}
                            />
                        )}
                        <div
                            className={
                                'flex flex-col  gap-y-2 pt-3.5 h-[200px]'
                            }>
                            {ITEMS.map((item) => {
                                return (
                                    <div
                                        key={item.text}
                                        className={'flex flex-col'}>
                                        <span
                                            className={
                                                'font-bold text-xs text-CATASCAN-text-basic-tertiary'
                                            }>
                                            {item.text}
                                        </span>
                                        <span
                                            className={
                                                item.text === 'Result'
                                                    ? 'mt-0.5'
                                                    : ''
                                            }>
                                            {item.text === 'Result'
                                                ? renderUtil.renderAiResultBadge(
                                                      item?.content,
                                                      'rounded-full text-sm'
                                                  )
                                                : item.content}
                                        </span>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                </div>
                <div className={'flex flex-col gap-y-2.5'}>
                    <span className={'font-medium text-lg'}>Confirmation</span>
                    <div className={'flex flex-col gap-y-3'}>
                        <span className={'text-xs text-muted-foreground'}>
                            The medical team should confirm the results above.
                        </span>
                        <div
                            className={
                                'p-4 bg-CATASCAN-background-baseline  rounded-lg'
                            }>
                            {/*TODO: check user value*/}
                            <FormField
                                control={form.control}
                                name={`${eye}EyeDiagnosis`}
                                render={({ field }) => {
                                    return (
                                        <RadioGroup
                                            onValueChange={field.onChange}
                                            defaultValue={
                                                form.getValues()[
                                                    `${eye}EyeDiagnosis`
                                                ] ?? undefined
                                            }>
                                            {RADIO_ITEMS.map((item) => {
                                                return (
                                                    <div key={item.value}>
                                                        <FormItem className="flex items-center space-y-0 gap-x-2">
                                                            <FormControl>
                                                                <RadioGroupItem
                                                                    value={
                                                                        item.value
                                                                    }
                                                                    id={
                                                                        item.value
                                                                    }
                                                                />
                                                            </FormControl>
                                                            <FormLabel
                                                                className={
                                                                    'font-normal'
                                                                }>
                                                                <span
                                                                    className={
                                                                        'flex gap-x-2 items-center'
                                                                    }>
                                                                    <Image
                                                                        width={
                                                                            16
                                                                        }
                                                                        height={
                                                                            16
                                                                        }
                                                                        src={
                                                                            item.imageUrl
                                                                        }
                                                                        alt={
                                                                            item.value
                                                                        }
                                                                    />
                                                                    {item.label}
                                                                </span>
                                                            </FormLabel>
                                                        </FormItem>
                                                    </div>
                                                );
                                            })}
                                        </RadioGroup>
                                    );
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div className={'flex flex-col gap-y-1.5 mt-2.5'}>
                    <span className={'font-medium text-lg'}>Remark</span>
                    <FormField
                        control={form.control}
                        name={`${eye}EyeRemarks`}
                        render={({ field }) => {
                            return (
                                <Textarea
                                    className={'resize-none'}
                                    defaultValue={
                                        form.getValues()[`${eye}EyeRemarks`] ??
                                        ''
                                    }
                                    onChange={field.onChange}
                                />
                            );
                        }}
                    />
                </div>
            </div>
        </div>
    );
}

export default ScanTabContents;
