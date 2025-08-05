import restClient from '@/lib/api/restClient';
import apiUrl from '@/lib/api/apiUrl';
import { ReportDetail } from '@/lib/types/schema';

const apiManager = {
    // Auth
    login: (props: ApiRequests.Login) => {
        const params = { ...props };

        return restClient.post<ApiResponses.Login>(
            apiUrl.getAuthLoginUrl(),
            params
        );
    },
    refreshAccessToken: ({ refreshToken }: ApiRequests.RefreshAccessToken) => {
        const params = { refreshToken };

        return restClient.post<ApiResponses.RefreshToken>(
            apiUrl.getAuthRefreshUrl(),
            params
        );
    },

    // Patients
    getPatientsList: (props: ApiRequests.GetPatientList) => {
        const params = { ...props, page: 0 };

        return restClient.get<ApiResponses.GetPatientsList>(
            apiUrl.getPatientsUrl(),
            params
        );
    },
    getPatient: ({ patientId }: ApiRequests.GetPatient) => {
        const params = {};

        return restClient.get<ApiResponses.GetPatient>(
            apiUrl.getPatientsDataPatientIdUrl(patientId),
            params
        );
    },
    // createPatient: (props: PatientDetail) => {
    createPatient: (props: ApiRequests.CreatePatient) => {
        const params = {
            ...props,
            dateOfBirth: props.dateOfBirth.toISOString(),
        };

        return restClient.post<ApiResponses.CreatePatient>(
            apiUrl.getPatientsUrl(),
            params
        );
    },
    updatePatient: (
        patientId: number,
        patientInfo: ApiRequests.UpdatePatient
    ) => {
        const params = {
            ...patientInfo,
            dateOfBirth: patientInfo.dateOfBirth.toISOString(),
        };

        return restClient.put<ApiResponses.UpdatePatient>(
            apiUrl.getPatientsPatientIdUrl(`${patientId}`),
            params
        );
    },

    updatePatientProfile: (
        patientId: number,
        healthInfo: ApiRequests.UpdatePatientProfile
    ) => {
        const nonMedicalInfo = {
            remarks: healthInfo.remarks,
        };

        const params = {
            ...nonMedicalInfo,
            healthInfo,
            patientId,
        };

        return restClient.put(
            apiUrl.getPatientsPatientIdProfileUrl(patientId),
            params
        );
    },

    deletePatient: (patientId: number) => {
        return restClient.delete(
            apiUrl.getPatientsPatientIdUrl(`${patientId}`)
        );
    },

    restorePatient: (patientId: number) => {
        return restClient.patch(apiUrl.getPatientsPatientIdUrl(`${patientId}`))
    },

    downloadPatientsCSV: (params: ApiRequests.GetPatientList) => {
        return restClient.get<ApiResponses.GetPatientListAsCSV>(
            apiUrl.getPatientsExportUrl(),
            params
        );
    },

    // User
    createUser: (props: ApiRequests.CreateUser) => {
        const params = { ...props };

        return restClient.post<ApiResponses.CreateUser>(
            apiUrl.getAdminUserUrl(),
            params
        );
    },
    deleteUser: ({ id }: ApiRequests.DeleteUser) => {
        const params = {};

        return restClient.delete<ApiResponses.DeleteUser>(
            apiUrl.getAdminUserIdUrl(id),
            params
        );
    },
    getUserData: ({ id }: ApiRequests.GetUserData) => {
        const params = {};

        return restClient.get<ApiResponses.GetUserData>(
            apiUrl.getAdminUserIdUrl(id),
            params
        );
    },
    updateUserData: ({
        id,
        password,
        username,
        institutionName,
    }: ApiRequests.UpdateUserData) => {
        const params = { username, password, institutionName };

        return restClient.patch<ApiResponses.UpdateUserData>(
            apiUrl.getAdminUserIdUrl(id),
            params
        );
    },

    uploadImage: ({
        file,
        imageId,
        side,
        aiResult,
    }: {
        file: string;
        imageId: string;
        side: string;
        aiResult: string;
    }) => {
        const params = { file, imageId, side, aiResult };
        return restClient.post('/eye-images', params);
    },

    //
    getReportByPatient: (patientId: number) => {
        const params = {};
        return restClient.get(
            apiUrl.getReportsPatientPatientIdUrl(patientId),
            params
        );
    },
    updateReportById: (reportId: number, report: ReportDetail) => {
        const params = {
            leftEyeRemarks: report.leftEyeRemarks,
            leftEyeDiagnosis: report.leftEyeDiagnosis,
            rightEyeRemarks: report.rightEyeRemarks,
            rightEyeDiagnosis: report.rightEyeDiagnosis,
        };

        return restClient.put(
            apiUrl.getReportsReportIdUrl(`${reportId}`),
            params
        );
    },
    linkReportWithPatient: (reportId: string, patientId: string) => {
        const params = {};
        return restClient.post<ApiResponses.LinkReportWithPatient>(
            apiUrl.getReportsReportIdLinkPatientPatientIdUrl(
                reportId,
                patientId
            ),
            params
        );
    },
    updateReportComment: (reportId: string, comments: string | null) => {
        const params = { comments };
        return restClient.put<ApiResponses.GetReportComments>(
            apiUrl.getReportsReportIdCommentsUrl(reportId),
            params
        );
    },
    // downloadFileStream: (endpoint: string, institutionId?: string) => {
    //     const params = {institutionId}

    //     return restClient.postBlob(endpoint, params);
    // },
    downloadReportImages: (institutionId?: string) => {
        const params = { institutionId }
        
        if (institutionId === 'all') {
            delete params.institutionId;
        }

        return restClient.postBlob(apiUrl.getAdminInstitutionsData2Url(), params);
    },
};
export default apiManager;
