/* eslint-disable @typescript-eslint/no-empty-object-type */

import {
    commonPatientHealthInfo,
    HealthInfo,
    UserAccount,
} from '@/lib/types/schema';

declare global {
    namespace ApiRequests {
        interface Pagination {
            page?: number;
            size?: number;
        }

        interface SortDir {
            sortDir: 'desc' | 'asc';
        }

        interface ReportSortOption {
            sortBy: 'name' | 'scanDate' | 'updatedAt';
        }

        interface DateRange {
            startDate?: string;
            endDate?: string;
        }

        interface Login {
            username: string;
            password: string;
        }

        interface RefreshAccessToken {
            refreshToken: string;
        }

        interface GetPatient {
            patientId: string;
        }

        interface CreatePatient {
            name: string;
            sex: string;
            phoneNum: string;
            // registrationDate: string; // date-time
            registrationDate: Date; // date-time
            // dateOfBirth: string;
            dateOfBirth: Date;
            remarks: string;
            referral?: string;
            followup?: string;
            healthInfo: commonPatientHealthInfo;
        }

        interface CreateUser extends UserAccount {
            password: string;
        }

        interface DeleteUser {
            id: number;
        }

        interface GetUserData {
            id: number;
        }

        interface UpdateUserData extends UserAccount {
            id: number;
            password: string;
        }

        interface GetReportListFromDateRange
            extends SortDir,
                ReportSortOption,
                Pagination,
                DateRange {}

        interface GetPatientList extends Pagination {
            sortBy?: 'name' | 'dateOfBirth' | 'createdAt' | 'updatedAt';
            sortDir?: 'asc' | 'desc';
            sex?: 'male' | 'female' | null;
            dateOfBirthFrom?: string; // "1950-10-01"
            dateOfBirthTo?: string; // '1990-05-02';
            startDate?: string; //"2014-04-29",
            endDate?: string; // "2025-10-29",
            dataStatus?: 0 | 1;
        }

        interface getFullPatientData {
            patientId: number;
        }

        interface UpdatePatientProfile extends HealthInfo {}

        interface UpdatePatient {
            name: string;
            dateOfBirth: Date;
            phoneNum: string;
            registrationDate: Date;
        }

        interface GetReportByPatient
            extends Pagination,
                DateRange,
                SortDir,
                ReportSortOption {
            linkStatus?: 0 | 1;
        }
    }
}
