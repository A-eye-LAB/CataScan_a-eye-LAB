const apiUrl = {
    /* API URLS */

    /* for getting image */
    getApiImageUrl: () => {
        return `/api/image`;
    },

    /* Auth */
    getAuthLoginUrl: () => {
        return `/auth/login`;
    },
    getAuthRefreshUrl: () => {
        return `/auth/refresh`;
    },

    /* Admin */
    getAdminUserUrl: () => {
        return `/admin/user`;
    },
    getAdminUserIdUrl: (userId: number) => {
        return `/admin/user/${userId}`;
    },
    getAdminUsersUrl: () => {
        return `/admin/users`;
    },
    getAdminPatientsUrl: () => {
        return `/admin/patients`;
    },
    getAdminInsitutionsUrl: () => {
         return `/admin/institutions`
    },
    getAdminInstitutionInstitutionIdPatientsPatientIdUrl: (
        institutionId: string,
        patientId: string
    ) => {
        return `/admin/institutions/${institutionId}/patients/${patientId}`;
    },
    getAdminInstitutionInstitutionIdPatientsPatientIdProfileUrl: (
        institutionId: string,
        patientId: string
    ) => {
        return `/admin/institutions/${institutionId}/patients/${patientId}/profile`;
    },
    getAdminReportsUrl: () => {
        return `/admin/reports`
    },
    getAdminReportsReportIdUrl: (reportId: string) => {
        return `/admin/reports/${reportId}`;
    },
    getAdminInstitutionInstitutionIdPatientsPatientIdReportsUrl: (institutionId: string, patientId: number) => {
        return `/admin/institutions/${institutionId}/patients/${patientId}/reports`
    },
    getAdminInstitutionsData2Url: () => {
        return `/admin/institutions/data2`;
    },

    /* Patients */
    getPatientsUrl: () => {
        return `/patients`;
    },
    getPatientsPatientIdUrl: (patientId: string) => {
        return `/patients/${patientId}`;
    },
    getPatientsPatientIdProfileUrl: (patientId: number) => {
        return `/patients/${patientId}/profile`;
    },
    getPatientsDataPatientIdUrl: (patientId: string) => {
        return `/patients/data/${patientId}`;
    },
    getPatientsExportUrl: () => {
        return `/patients/export`;
    },

    /* Reports */
    getReportsUrl: () => {
        return `/reports`;
    },
    getReportsReportIdUrl: (reportId: string) => {
        return `/reports/${reportId}`;
    },
    getReportsPatientPatientIdUrl: (patientId: number) => {
        return `/reports/patient/${patientId}`;
    },
    getReportsReportIdCandidatesListUrl: (reportId: string) => {
        return `/reports/${reportId}/candidates-list`;
    },
    getReportsReportIdCommentsUrl: (reportId: string) => {
        return `/reports/${reportId}/comments`;
    },
    getReportsReportIdLinkPatientPatientIdUrl: (
        reportId: string,
        patientId: string
    ) => {
        return `/reports/${reportId}/link-patient/${patientId}`;
    },
};

export default apiUrl;
