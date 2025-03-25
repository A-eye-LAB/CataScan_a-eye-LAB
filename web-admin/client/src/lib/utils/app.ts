import { ReportDetail } from '@/lib/types/schema';

const appUtil = {
    getEyeStatus: (data: ReportDetail['leftEyeDiagnosis']) => {
        if (data?.includes('abnormal') || data?.includes('Abnormal')) {
            return 'probably-cataract';
        } else if (data?.includes('requires')) {
            return 'certainly-cataract';
        } else if (data?.includes('normal')) {
            return 'certainly-normal';
        } else {
            return 'uncertain';
        }
    },
    downloadCSV: (csvData: string) => {
        const blob = new Blob([csvData], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'patients.csv');
        document.body.appendChild(link);
        link.click();

        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    },
};

export default appUtil;
