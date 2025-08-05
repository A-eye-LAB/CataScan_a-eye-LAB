import { useEffect, useState } from 'react';
import { useForm, FormProvider } from 'react-hook-form';
import { toast } from 'sonner';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { mutate as globalMutate } from 'swr';
import ScanTabContents from '@/components/patients/reports-tab/scan-tab-contents';
import useReportDetail from '@/hooks/api/use-report-detail';
import {
    AlertDialog,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogCancel,
    AlertDialogAction,
} from '@/components/ui/alert-dialog';
import { FormMessage } from '@/components/ui/form';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { TDialogProps } from '@/lib/types/ui';
import { ReportDetail, reportDetailSchema } from '@/lib/types/schema';
import apiManager from '@/lib/api/apiManager';
import { validateStatus } from '@/lib/api/restClient';

type TScanResultDialogProps = TDialogProps & {
    defaultValue?: 'left' | 'right';
    reportId: number;
};

function ScanResultDialog(props: TScanResultDialogProps) {
    const { isOpenDialog, setIsOpenDialog, defaultValue, reportId } = props;
    const { report, mutate } = useReportDetail(`${reportId}`, isOpenDialog);

    const scanResultForm = useForm<ReportDetail>({
        resolver: zodResolver(reportDetailSchema),
        defaultValues: { ...report },
    });

    const [activeTab, setActiveTab] = useState<'left' | 'right'>('left');

    const onSubmit = async (data: z.infer<typeof reportDetailSchema>) => {
        try {
            const result = await apiManager.updateReportById(reportId, data);

            if (validateStatus(result.status)) {
                await mutate();
                toast('Success');
                scanResultForm.reset();
                setIsOpenDialog(false);

                // Mutate for hook: useReportByPatient
                await globalMutate((key) => {
                    if (Array.isArray(key) && typeof key[0] === 'string') {
                        return (
                            key[0].includes('/reports/patient/') ||
                            (key[0].includes('/patients/') &&
                                key[0].includes('/reports'))
                        );
                    }
                    return false;
                });
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        }
    };

    useEffect(() => {
        if (report && isOpenDialog) {
            scanResultForm.reset({ ...report });
            if (defaultValue) {
                setActiveTab(defaultValue);
            }
        }
    }, [defaultValue, isOpenDialog, report, scanResultForm]);

    return (
        <AlertDialog open={isOpenDialog} onOpenChange={setIsOpenDialog}>
            <AlertDialogContent
                onClick={(event) => {
                    event.stopPropagation();
                }}>
                <FormProvider {...scanResultForm}>
                    <form>
                        <AlertDialogHeader className={'mb-6'}>
                            <AlertDialogTitle>
                                <span className={'font-semibold text-2xl'}>
                                    Scan Result
                                </span>
                            </AlertDialogTitle>
                        </AlertDialogHeader>
                        <AlertDialogDescription className={'hidden'} />
                        <Tabs
                            defaultValue={activeTab}
                            onValueChange={(value) =>
                                setActiveTab(value as 'left' | 'right')
                            }>
                            <TabsList>
                                <TabsTrigger value={'left'}>
                                    Left Eye
                                </TabsTrigger>
                                <TabsTrigger value={'right'}>
                                    Right Eye
                                </TabsTrigger>
                            </TabsList>
                            <TabsContent
                                value="left"
                                forceMount
                                className={
                                    activeTab !== 'left' ? 'hidden' : ''
                                }>
                                <ScanTabContents eye={'left'} />
                            </TabsContent>
                            <TabsContent
                                value="right"
                                forceMount
                                className={
                                    activeTab !== 'right' ? 'hidden' : ''
                                }>
                                <ScanTabContents eye={'right'} />
                            </TabsContent>
                        </Tabs>
                        <AlertDialogFooter className={'mt-5'}>
                            <AlertDialogCancel
                                onClick={() => {
                                    scanResultForm.reset();
                                }}>
                                {'Cancel'}
                            </AlertDialogCancel>
                            <AlertDialogAction
                                type="button"
                                variant="catascan"
                                onClick={(event) => {
                                    scanResultForm.handleSubmit(onSubmit)();
                                    event.preventDefault();
                                }}>
                                Save
                            </AlertDialogAction>
                            <FormMessage />
                        </AlertDialogFooter>
                    </form>
                </FormProvider>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export default ScanResultDialog;
