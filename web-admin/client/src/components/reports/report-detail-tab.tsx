'use client';

import { useState } from 'react';
import Image from 'next/image';
import { Pencil2Icon } from '@radix-ui/react-icons';
import ScanResultDialog from '@/components/patients/scan-result-dialog';
import useDialog from '@/hooks/use-dialog';
import { Button } from '@/components/ui/button';
import { ReportDetail } from '@/lib/types/schema';
import renderUtil from '@/lib/utils/renderUtils';

type TReportDetailTabProps = {
    report: ReportDetail;
};

function ReportDetailTab(props: TReportDetailTabProps) {
    const { report } = props;
    const { isOpen, open, setIsOpen } = useDialog(false);
    const [defaultEye, setDefaultEye] = useState<'left' | 'right'>('left');

    const REPORTS = [
        {
            label: 'l',
            status: report.leftAiResult,
            image: report.leftEyeImageFilePath,
        },
        {
            label: 'r',
            status: report.rightAiResult,
            image: report.rightEyeImageFilePath,
        },
    ];

    return (
        <div className="flex gap-x-3">
            {REPORTS.map((report) => (
                <div
                    key={report.label}
                    className="flex flex-col flex-grow items-center gap-y-12 relative py-12
                       rounded-md border border-[#d9d9d9]">
                    <div
                        className="flex items-center justify-center w-[39px] h-[39px]
                            rounded-full border border-[#E5E5E5] text-sm font-medium
                            absolute top-[20px] left-[20px]">
                        <span className="font-bold">
                            {report.label.toUpperCase()}
                        </span>
                    </div>
                    <div>
                        <span className={report.status ? '' : 'invisible'}>
                            {renderUtil.renderAiResultBadge(
                                report.status,
                                'rounded-full text-lg'
                            )}
                        </span>
                    </div>
                    <div className={'w-[252px] h-[291px]'}>
                        {report.image ? (
                            <Image
                                src={report.image}
                                alt="Eye Image"
                                width={252}
                                height={291}
                            />
                        ) : (
                            <div className='w-[252px] h-[291px] border-2 border-#d9d9d9 rounded-md flex justify-center items-center'>
                                <span className='text-muted-foreground'>No Data</span>
                            </div>
                        )}
                    </div>
                    <div>
                        <Button
                            variant="secondary"
                            size="xl"
                            onClick={() => {
                                setDefaultEye(
                                    report.label === 'l' ? 'left' : 'right'
                                );
                                open();
                            }}
                            disabled={!report}
                            >
                            <Pencil2Icon />
                            Remark
                        </Button>
                    </div>
                </div>
            ))}
            <ScanResultDialog
                isOpenDialog={isOpen}
                setIsOpenDialog={setIsOpen}
                defaultValue={defaultEye}
                reportId={report.reportId}
            />
        </div>
    );
}

export default ReportDetailTab;
