import { useState } from 'react';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { Row } from '@tanstack/react-table';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { RegisteredPatient } from '@/lib/types/schema';
import { cn } from '@/lib/utils/tailwind';
import apiManager from '@/lib/api/apiManager';

type TReportPatientProfileActions = {
    row: Row<RegisteredPatient>;
    reportId: string;
};

function ReportPatientProfileActions(props: TReportPatientProfileActions) {
    const { row, reportId } = props;

    const [isLinked, setIsLinked] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState(false);

    const onLink = async () => {
        try {
            setIsLoading(true);

            const result = await apiManager.linkReportWithPatient(
                reportId,
                `${row.original.patientId}`
            );

            if (result.data.patientId) {
                setIsLinked(true);
            }
            toast('Success');
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsLoading(false);
        }
    };

    if (isLinked) {
        return (
            <Badge
                variant={'checked'}
                className={cn('rounded-md w-[60px] h-[32px]')}>
                Linked
            </Badge>
        );
    } else {
        return (
            <div className={'w-[56px] h-[32px]'}>
                <Button
                    variant={'outline'}
                    className={
                        'border-CATASCAN-button text-CATASCAN-button w-[56px] h-[32px] font-bold'
                    }
                    onClick={onLink}>
                    {isLoading ? (
                        <Loader2 className={'animate-spin'} />
                    ) : (
                        '+ Link'
                    )}
                </Button>
            </div>
        );
    }
}

export default ReportPatientProfileActions;
