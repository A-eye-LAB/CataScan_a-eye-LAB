import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
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
import { Form, FormMessage } from '@/components/ui/form';
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
                setIsOpenDialog(false);
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
                <Form {...scanResultForm}>
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
                                <ScanTabContents
                                    form={scanResultForm}
                                    eye={'left'}
                                />
                            </TabsContent>
                            <TabsContent
                                value="left"
                                forceMount
                                className={
                                    activeTab !== 'right' ? 'hidden' : ''
                                }>
                                <ScanTabContents
                                    form={scanResultForm}
                                    eye={'right'}
                                />
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
                </Form>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export default ScanResultDialog;
