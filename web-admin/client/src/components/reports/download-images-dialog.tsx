import { useState } from 'react';
import { toast } from 'sonner';
import { useSession } from 'next-auth/react';
import { Loader2 } from 'lucide-react';
import useAdminInstitutions from '@/hooks/api/use-admin-institutions';
import { Button } from '@/components/ui/button';
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import {
    SelectTrigger,
    SelectValue,
    SelectContent,
    SelectItem,
    Select,
} from '@/components/ui/select';
import apiManager from '@/lib/api/apiManager';

function DownloadImagesDialog() {
    const role = useSession().data?.user?.role;
    const { institutions } = useAdminInstitutions(role === 'admin');
    const [selectedInstitutionId, setSelectedInstitutionId] = useState<
        string | undefined
    >();
    const [isLoading, setIsLoading] = useState(false);

    const downloadStreamingFile = async (
        institutionId: string,
        institutionName: string
    ) => {
        const response = await apiManager.downloadReportImages(institutionId);

        const downloadUrl = window.URL.createObjectURL(response.data);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = `${institutionName}-report-images.zip`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(downloadUrl);
    };

    const handleDownload = async () => {
        setIsLoading(true);

        const selectedInstitutionName =
            selectedInstitutionId === 'all'
                ? 'All'
                : institutions?.find(
                      (inst) =>
                          inst.institutionId.toString() ===
                          selectedInstitutionId
                  )?.institutionName;

        try {
            await downloadStreamingFile(
                selectedInstitutionId as string,
                selectedInstitutionName as string
            );
        } catch (error) {
            console.error(error);
            toast('Error: Check console for details');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Dialog>
            <DialogTrigger asChild>
                <Button variant={'catascan'}>Download Images</Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Download Images</DialogTitle>
                    <DialogDescription>
                        {'Select the institution to download the image.'}
                    </DialogDescription>
                </DialogHeader>
                <div className="">
                    <div className="grid gap-3">
                        <Label htmlFor="name-1">Name</Label>
                        <Select onValueChange={setSelectedInstitutionId}>
                            <SelectTrigger>
                                <SelectValue placeholder="Select institution" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="all">All</SelectItem>

                                {institutions &&
                                    institutions.map((inst) => (
                                        <SelectItem
                                            key={inst.institutionId}
                                            value={inst.institutionId.toString()}>
                                            {inst.institutionName}
                                        </SelectItem>
                                    ))}
                            </SelectContent>
                        </Select>
                    </div>
                </div>
                <DialogFooter>
                    <DialogClose asChild>
                        <Button variant="outline">Cancel</Button>
                    </DialogClose>
                    <Button
                        variant={'catascan'}
                        onClick={handleDownload}
                        disabled={!selectedInstitutionId || isLoading}>
                        {isLoading ? (
                            <Loader2 className={'animate-spin'} />
                        ) : (
                            'Download'
                        )}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}

export default DownloadImagesDialog;
