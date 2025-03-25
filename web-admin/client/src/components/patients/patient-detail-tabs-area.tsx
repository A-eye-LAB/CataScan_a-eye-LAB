'use client';

import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Loader2 } from 'lucide-react';
import { toast } from 'sonner';
import { zodResolver } from '@hookform/resolvers/zod';
import HealthTab from '@/components/patients/patient-information-dialog/health-tab';
import ReportsTab from '@/components/patients/reports-tab';
import usePatientHealthInfo from '@/hooks/api/use-patient-health-info';
import Error from '@/components/common/error';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Form } from '@/components/ui/form';
import { Separator } from '@/components/ui/separator';
import { Button } from '@/components/ui/button';
import { DEFAULT_VALUES } from '@/lib/constants';
import { patientHealthInfoSchema } from '@/lib/types/schema';
import apiManager from '@/lib/api/apiManager';

type TPatientDetailTabsArea = {
    patientId: number;
};

function PatientDetailTabsArea(props: TPatientDetailTabsArea) {
    const { patientId } = props;

    const { healthInfo, isLoading, error, mutate } =
        usePatientHealthInfo(patientId);

    const patientHealthForm = useForm<z.infer<typeof patientHealthInfoSchema>>({
        resolver: zodResolver(patientHealthInfoSchema),
        defaultValues: { ...DEFAULT_VALUES.healthInfo },
    });

    const [isFormSubmitting, setIsFormSubmitting] = useState(false);
    const onSubmit = async (data: z.infer<typeof patientHealthInfoSchema>) => {
        setIsFormSubmitting(true);
        try {
            const result = await apiManager.updatePatientProfile(patientId, {
                ...data,
            });

            if (result.data) {
                toast('Success');
                await mutate();
            }
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsFormSubmitting(false);
        }
    };

    useEffect(() => {
        if (healthInfo) {
            patientHealthForm.reset({ ...healthInfo });
        }
    }, [healthInfo, patientHealthForm]);

    return (
        <div className={'border border-[#d9d9d9] p-5'}>
            <Tabs defaultValue="health">
                <TabsList>
                    <TabsTrigger value="health">Health Information</TabsTrigger>
                    <TabsTrigger value="reports">Reports</TabsTrigger>
                </TabsList>
                <Separator className={'my-3'} />
                <TabsContent value="health">
                    {error ? (
                        <Error error={error} reset={mutate} />
                    ) : isLoading ? (
                        <Loader2 className={'animate-spin'} />
                    ) : (
                        <Form {...patientHealthForm}>
                            <form
                                onSubmit={patientHealthForm.handleSubmit(
                                    onSubmit
                                )}>
                                <HealthTab form={patientHealthForm} />
                                <Button
                                    type={'submit'}
                                    disabled={isFormSubmitting}>
                                    {isFormSubmitting ? (
                                        <Loader2 className={'animate-spin'} />
                                    ) : (
                                        'Save'
                                    )}
                                </Button>
                            </form>
                        </Form>
                    )}
                </TabsContent>
                <TabsContent value="reports" className={'w-full'}>
                    <ReportsTab patientId={patientId} />
                </TabsContent>
            </Tabs>
        </div>
    );
}

export default PatientDetailTabsArea;
