/* eslint-disable @typescript-eslint/no-empty-object-type */
import {
    HealthInfo,
    RegisteredPatient,
    RegisteredUserAccount,
    Report,
    ReportDetail,
    UserAccount,
    Institution,
} from '@/lib/types/schema';

declare global {
    namespace ApiResponses {
        interface PaginationResult {
            currentPage: number;
            totalPage: number;
            totalElements: number;
            pageSize: number;
        }

        interface Login {
            token: string;
        }

        interface RefreshToken {
            token: string;
        }

        type GetPatientsList = RegisteredPatient[];

        interface GetPatient extends RegisteredPatient {}

        interface CreatePatient extends RegisteredPatient {}

        interface UpdatePatient extends RegisteredPatient {}

        type GetUserList = PaginationResult & RegisteredUserAccount[];

        interface CreateUser extends UserAccount {}

        interface DeleteUser {}

        interface GetUserData extends RegisteredUserAccount {}

        interface UpdateUserData {}

        interface GetReportListFromDateRange extends Report {}

        interface GetPatientProfiles {
            healthInfo: HealthInfo;
        }

        interface GetReportsById extends ReportDetail {}

        type GetReportByPatient = ReportDetail[];

        type GetReportCandidatePatients = RegisteredPatient[];

        interface GetReportComments {
            reportId: number;
            comments: null | string;
        }

        interface LinkReportWithPatient {
            patientId: number;
            patientName: string;
            reportId: number;
            message: string;
        }

        type GetPatientListAsCSV = string;

        type GetAdminInstitutionList = Institution[];
    }
}
