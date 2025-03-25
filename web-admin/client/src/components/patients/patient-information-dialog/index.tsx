import { UseFormReturn } from 'react-hook-form';
import { Loader2 } from 'lucide-react';
import BasicTab from '@/components/patients/patient-information-dialog/basic-tab';
import HealthTab from '@/components/patients/patient-information-dialog/health-tab';
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogFooter,
    AlertDialogDescription,
} from '@/components/ui/alert-dialog';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Form } from '@/components/ui/form';
import { Separator } from '@/components/ui/separator';
import { PATIENT } from '@/lib/constants';
import { PatientDetail } from '@/lib/types/schema';
import { TDialogProps } from '@/lib/types/ui';

type TBaseDialogProps = TDialogProps & {
    mode: (typeof PATIENT.DIALOG_MODE)[number];
    isSubmitting: boolean;
    form: UseFormReturn<PatientDetail>;
    onSubmit: (data: PatientDetail) => Promise<void>;
};

function PatientInformationDialog(props: TBaseDialogProps) {
    const {
        isOpenDialog,
        setIsOpenDialog,
        form,
        onSubmit,
        isSubmitting,
        mode,
    } = props;

    return (
        <AlertDialog open={isOpenDialog} onOpenChange={setIsOpenDialog}>
            <AlertDialogContent>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)}>
                        <AlertDialogHeader>
                            <AlertDialogTitle>
                                {mode === 'Add' ? 'Add' : 'Edit'} Patient
                                Information
                            </AlertDialogTitle>
                            <AlertDialogDescription></AlertDialogDescription>
                        </AlertDialogHeader>

                        <Tabs defaultValue="basic" className="h-[542px]">
                            <TabsList>
                                <TabsTrigger value="basic">
                                    Basic Information
                                </TabsTrigger>
                                {mode === 'Add' && (
                                    <TabsTrigger value="health">
                                        Health Information
                                    </TabsTrigger>
                                )}
                            </TabsList>
                            <Separator className={'my-3'} />
                            <TabsContent value="basic">
                                <BasicTab form={form} mode={mode} />
                            </TabsContent>
                            {mode === 'Add' && (
                                <TabsContent value="health">
                                    <HealthTab
                                        form={form}
                                        fieldNamePrefix="healthInfo"
                                    />
                                </TabsContent>
                            )}
                        </Tabs>

                        <AlertDialogFooter>
                            <AlertDialogCancel
                                onClick={() => {
                                    form.reset();
                                }}>
                                Cancel
                            </AlertDialogCancel>
                            <AlertDialogAction
                                variant={'catascan'}
                                type="submit"
                                disabled={isSubmitting}>
                                {isSubmitting ? (
                                    <>
                                        <Loader2 className={'animate-spin'} />
                                        Please wait
                                    </>
                                ) : (
                                    <>{mode === 'Add' ? 'Add' : 'Confirm'}</>
                                )}
                            </AlertDialogAction>
                        </AlertDialogFooter>
                    </form>
                </Form>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export default PatientInformationDialog;
