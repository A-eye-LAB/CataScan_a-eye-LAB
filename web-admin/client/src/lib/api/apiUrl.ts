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
    getAdminUserListUrl: () => {
        return `/admin/user-list`;
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
