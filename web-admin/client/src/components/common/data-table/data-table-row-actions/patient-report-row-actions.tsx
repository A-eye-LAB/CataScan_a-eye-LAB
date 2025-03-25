import { Row } from '@tanstack/react-table';
import { Pencil2Icon } from '@radix-ui/react-icons';
import ScanResultDialog from '@/components/patients/scan-result-dialog';
import useDialog from '@/hooks/use-dialog';
import { Button } from '@/components/ui/button';
import { ReportDetail } from '@/lib/types/schema';

type TPatientReportRowActionsProps = {
    row: Row<ReportDetail>;
};

function PatientReportRowActions(props: TPatientReportRowActionsProps) {
    const { row } = props;
    const { isOpen, open, setIsOpen } = useDialog(false);

    return (
        <div>
            <Button
                size={'icon'}
                variant={'icon'}
                onClick={(event) => {
                    event.stopPropagation();
                    open();
                }}>
                <Pencil2Icon />
            </Button>
            {/*    TODO: set dialog  */}
            <ScanResultDialog
                isOpenDialog={isOpen}
                setIsOpenDialog={setIsOpen}
                reportId={row.original.reportId}
            />
        </div>
    );
}

export default PatientReportRowActions;
