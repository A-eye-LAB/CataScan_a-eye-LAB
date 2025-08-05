'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { FileText, Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import ReportDetailTab from '@/components/reports/report-detail-tab';
import ReportsFilter from '@/components/reports/reports-filter';
import PatientInformationDialog from '@/components/patients/patient-information-dialog';
import useReportsCandidates from '@/hooks/api/use-reports-candidates';
import useReportDetail from '@/hooks/api/use-report-detail';
import useReportTableFilters from '@/hooks/use-report-table-filters';
import useDialog from '@/hooks/use-dialog';
import useReportComments from '@/hooks/api/use-report-comments';
import { usePatientManagement } from '@/hooks/use-patient-management';
import ErrorUI from '@/components/common/error';
import { reportsPatientProfilesColumns } from '@/components/common/data-table/columns';
import DataTable from '@/components/common/data-table';
import { Button } from '@/components/ui/button';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Separator } from '@/components/ui/separator';
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog';
import { Textarea } from '@/components/ui/textarea';
import apiManager from '@/lib/api/apiManager';
import { validateStatus } from '@/lib/api/restClient';
import { PatientDetail, patientDetailSchema } from '@/lib/types/schema';
import { DEFAULT_VALUES } from '@/lib/constants';

function ReportDetail() {
    const params = useParams();
    const { reportId } = params;

    const { report, isLoading, error } = useReportDetail(reportId as string);

    const {
        filters,
        tempFilters,
        updateTempFilters,
        applyFilters,
        resetTempFilters,
    } = useReportTableFilters();
    const { patients } = useReportsCandidates(reportId as string, filters);
    const { comments, mutate } = useReportComments(reportId as string);
    const { isOpen: isInfoOpen, setIsOpen: setIsInfoOpen } = useDialog(false);
    const { isOpen: isCommentOpen, setIsOpen: setIsCommentOpen } =
        useDialog(false);

    const [localComments, setLocalComments] = useState(comments ?? '');

    const patientForm = useForm<PatientDetail>({
        resolver: zodResolver(patientDetailSchema),
        defaultValues: {
            ...DEFAULT_VALUES.patientDetail,
            dateOfBirth: new Date(),
        },
    });

    const { isSubmitting, createPatient } = usePatientManagement({
        onCreateSuccess: () => {
            setIsInfoOpen(false);
            patientForm.reset();
        },
    });

    const onConfirm = async () => {
        try {
            const result = await apiManager.updateReportComment(
                reportId as string,
                localComments.trim().length < 1 ? null : localComments
            );

            if (validateStatus(result.status)) {
                toast('Success');
                setIsCommentOpen(false);
                await mutate();
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        }
    };

    const BASIC_INFORMATATIONS = [
        {
            name: 'reportId',
            label: 'Report Id',
            value: reportId as string,
        },
        {
            name: 'sex',
            label: 'Sex',
            value: report?.sex ?? '-',
        },

        {
            name: 'scanDate',
            label: 'Scan Date',
            value: report?.scanDate ?? '',
        },
        {
            name: 'status',
            label: 'Status',
            value: report?.linkStatus ? 'Linked' : 'Unlinked',
        },
    ];

    useEffect(() => {
        if (comments) {
            setLocalComments(comments);
        }
    }, [comments]);

    if (isLoading) {
        return <Loader2 className={'animate-spin'} />;
    }

    if (!report || !reportId || error) {
        return <ErrorUI error={error} reset={() => {}} />;
    }

    return (
        <div
            className={'h-full border bg-background border-[#d9d9d9] p-6 px-5'}>
            <div className={'flex flex-col gap-y-4'}>
                <div className={'flex flex-col gap-y-6 py-6 px-5'}>
                    <div className={'flex'}>
                        <h1
                            className={
                                'h-[30px] text-CATASCAN-text-basic-heading font-bold text-2xl'
                            }>
                            {report.patientName}
                        </h1>
                        <Dialog
                            open={isCommentOpen}
                            onOpenChange={setIsCommentOpen}>
                            <DialogTrigger asChild>
                                <Button
                                    variant={'icon'}
                                    className={'p-2 hover:bg-primary/10'}>
                                    <FileText
                                        className={
                                            comments
                                                ? 'opacity-100'
                                                : 'opacity-30'
                                        }
                                    />
                                </Button>
                            </DialogTrigger>
                            <DialogContent className="sm:max-w-[400px] gap-y-4">
                                <DialogHeader>
                                    <DialogTitle>
                                        Comment about Patient
                                    </DialogTitle>
                                    <DialogDescription className={'sr-only'} />
                                </DialogHeader>
                                <div className={'flex flex-col gap-y-1.5 mt-2'}>
                                    <span className={'font-medium'}>
                                        Comment
                                    </span>
                                    <Textarea
                                        className={
                                            'resize-none h-[80px] bg-[#F9FAFB]'
                                        }
                                        defaultValue={localComments}
                                        onChange={(event) => {
                                            setLocalComments(
                                                event.target.value
                                            );
                                        }}
                                    />
                                </div>
                                <Separator className={'mb-2'} />
                                <DialogFooter>
                                    <Button
                                        variant={'outline'}
                                        type="submit"
                                        onClick={() => {
                                            setIsCommentOpen(false);
                                        }}>
                                        Cancel
                                    </Button>
                                    <Button
                                        variant={'catascan'}
                                        type="submit"
                                        onClick={onConfirm}
                                        disabled={
                                            (comments ?? '')?.length ===
                                            localComments.length
                                        }>
                                        Confirm
                                    </Button>
                                </DialogFooter>
                            </DialogContent>
                        </Dialog>
                    </div>
                    <div className={'grid grid-cols-4 gap-x-5'}>
                        {BASIC_INFORMATATIONS.map((info, index) => {
                            return (
                                <div
                                    key={info.name}
                                    className={
                                        'flex align-middle justify-between'
                                    }>
                                    <div className={'flex flex-col'}>
                                        <span
                                            className={
                                                'text-CATASCAN-text-basic-tertiary font-semibold'
                                            }>
                                            {info.label}
                                        </span>
                                        <span
                                            className={
                                                'text-CATASCAN-text-basic-primary font-semibold'
                                            }>
                                            {info.value}
                                        </span>
                                    </div>
                                    {BASIC_INFORMATATIONS.length - 1 ===
                                    index ? (
                                        <></>
                                    ) : (
                                        <Separator
                                            orientation={'vertical'}
                                            className="h-[40px] bg-CATASCAN-border-basic-split"
                                        />
                                    )}
                                </div>
                            );
                        })}
                    </div>
                </div>
                <div className={'flex flex-col p-5'}>
                    <Tabs defaultValue={'detail'}>
                        <TabsList>
                            <TabsTrigger value="detail">
                                Report Detail
                            </TabsTrigger>
                            <TabsTrigger value="link">Link Patient</TabsTrigger>
                        </TabsList>
                        <TabsContent value="detail">
                            <ReportDetailTab report={report} />
                        </TabsContent>
                        <TabsContent value="link" className={'w-full'}>
                            <DataTable
                                columns={reportsPatientProfilesColumns(
                                    reportId as string
                                )}
                                data={patients ?? []}
                                searchProperty={'patientName'}
                                searchInputPlaceholder={'Search Patient Name'}
                                showPagination={false}
                                showToolbarTotal={false}
                                filterComponent={
                                    <div
                                        className={
                                            'w-full flex items-center justify-between'
                                        }>
                                        <ReportsFilter
                                            tempFilters={tempFilters}
                                            updateTempFilters={
                                                updateTempFilters
                                            }
                                            applyFilters={applyFilters}
                                            resetTempFilters={resetTempFilters}
                                        />

                                        <div
                                            className={
                                                'flex items-center gap-x-1.5'
                                            }>
                                            <span>
                                                No relevant results found?
                                            </span>
                                            <Button
                                                variant={'outline'}
                                                className={
                                                    'border-CATASCAN-button text-CATASCAN-button'
                                                }
                                                onClick={() => {
                                                    setIsInfoOpen(true);
                                                }}>
                                                Add Patient Profile
                                            </Button>
                                            <PatientInformationDialog
                                                isOpenDialog={isInfoOpen}
                                                setIsOpenDialog={setIsInfoOpen}
                                                mode={'Add'}
                                                form={patientForm}
                                                isSubmitting={isSubmitting}
                                                onSubmit={createPatient}
                                            />
                                        </div>
                                    </div>
                                }
                                emptyElement={<div className="h-[416px]"></div>}
                            />
                        </TabsContent>
                    </Tabs>
                </div>
            </div>
        </div>
    );
}

export default ReportDetail;
