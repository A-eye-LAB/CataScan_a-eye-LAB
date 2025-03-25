import Image from 'next/image';
import useReportByPatient from '@/hooks/api/use-report-by-patient';
import DataTable from '@/components/common/data-table';
import { patientReportsColumns } from '@/components/common/data-table/columns';
import imageUtil from '@/lib/utils/image';

type TReportsTabProps = { patientId: number };

function ReportsTab(props: TReportsTabProps) {
    const { patientId } = props;

    const { reports } = useReportByPatient(patientId, {
        sortDir: 'desc',
        sortBy: 'scanDate',
    });

    return (
        <DataTable
            columns={patientReportsColumns}
            data={reports ?? []}
            showToolbarTotal={false}
            showPagination={false}
            renderExpandedRow={(row) => {
                const images = [
                    {
                        label: 'l',
                        image: row.original.leftEyeImageFilePath,
                    },
                    {
                        label: 'r',
                        image: row.original.rightEyeImageFilePath,
                    },
                ];

                return (
                    <div className={'py-7 bg-CATASCAN-background-baseline'}>
                        <div className={'flex justify-center gap-x-7'}>
                            {images.map((imageData) => {
                                return (
                                    <div
                                        key={imageData.label}
                                        className={'flex gap-x-5'}>
                                        <div
                                            className="flex items-center justify-center w-[39px] h-[39px]
                            rounded-full border border-[#E5E5E5] text-sm font-medium
                            ">
                                            <span className="font-bold">
                                                {imageData.label.toUpperCase()}
                                            </span>
                                        </div>
                                        <div className={'w-[185px] h-[185px]'}>
                                            {imageData.image ? (
                                                <Image
                                                    src={imageUtil.getInternalImageUrl(
                                                        imageData.image
                                                    )}
                                                    alt={'eye image'}
                                                    width={185}
                                                    height={185}
                                                />
                                            ) : (
                                                <></>
                                            )}
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                );
            }}
        />
    );
}

export default ReportsTab;
