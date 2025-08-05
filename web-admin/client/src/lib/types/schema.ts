import { z } from 'zod';

export const userAccountSchema = z.object({
    username: z.string(),
    institutionName: z.string(),
    // email: z.string().optional().nullable(),
});

export const registeredUserAccountSchema = userAccountSchema.extend({
    role: z.string(), // "user" | "admin"
    id: z.number(),
    createdDate: z.string(),
    updatedDate: z.string(),
});

// Password must be in the range of 8-32 characters
export const userAccountFormSchema = z
    .object({
        username: z
            .string()
            .min(1, 'Please type user name')
            .max(20, 'Username must not exceed 20 characters'),
        institutionName: z.string().min(1, 'Please type Institution'),
        email: z.string().optional().nullable(),
        role: z.string().optional(),
        password: z
            .string()
            .min(8, 'Password must be at least 8 characters')
            .max(32, 'Password must not exceed 32 characters'),
        confirmPassword: z.string().min(1, 'Please type confirm password'),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: 'Passwords do not match',
        path: ['confirmPassword'],
    });

export const commonPatientHealthInfoSchema = z.object({
    bloodSugarLevel: z.number(),
    diastolicBp: z.number(),
    dontKnow: z.boolean(),
    leftEyeVision: z.number(),
    rightEyeVision: z.number(),
    systolicBp: z.number(),
    visitDate: z.string(),
});
export const patientHealthInfoSchema = commonPatientHealthInfoSchema.extend({
    cataract: z.boolean(),
    diabetes: z.boolean(),
    hypertension: z.boolean(),
    remarks: z.string().optional(),
});

export const sexSchema = z.enum(['male', 'female', 'other']);

export const patientSchema = z.object({
    patientName: z.string().min(1, 'Please type profile name'),
    sex: sexSchema,
    dateOfBirth: z.date(),
    phoneNum: z.string(),
});

export const patientDetailSchema = patientSchema.extend({
    healthInfo: patientHealthInfoSchema,
});

export const registeredPatientSchema = patientSchema.extend({
    age: z.number(),
    // patientPk: z.number(),
    patientId: z.number(),
    registrationDate: z.string(),
    institutionId: z.number(),
    institutionName: z.string(),
});

const reportBaseSchema = z.object({
    reportId: z.number().int().nonnegative(),
    scanDate: z.string(),
});

export const aiResultSchema = z.enum([
    'lowRisk',
    'requiresAttention',
    'ungradable',
]);

const reportAiResultSchema = z.object({
    leftAiResult: aiResultSchema,
    rightAiResult: aiResultSchema,
});

export const reportSchema = reportBaseSchema
    .merge(reportAiResultSchema)
    .extend({
        linkStatus: z.union([z.literal(0), z.literal(1)]), // "unlinked, linked"
        patientName: z.string(),
        sex: z.enum(['male', 'female', 'other']).optional(),
    });

export const reportDetailSchema = reportSchema.extend({
    leftEyeImageFilePath: z.string().nullable(),
    rightEyeImageFilePath: z.string().nullable(),
    leftEyeDiagnosis: z.string().nullable(),
    rightEyeDiagnosis: z.string().nullable(),
    leftEyeRemarks: z.string().nullable(),
    rightEyeRemarks: z.string().nullable(),
});

export const reportPatientsSchema = z.object({
    patientName: z.string(),
    sex: z.string(),
    dateOfBirth: z.string(),
    phoneNumber: z.string(),
    status: z.enum(['linked', 'unlinked']),
});

export const institutionSchema = z.object({
    institutionId: z.number().int(),
    institutionName: z.string(),
    address: z.string(),
    createdDate: z.string(),
});

export type UserAccount = z.infer<typeof userAccountSchema>;
export type UserAccountForm = z.infer<typeof userAccountFormSchema>;
export type RegisteredUserAccount = z.infer<typeof registeredUserAccountSchema>;
export type Patient = z.infer<typeof patientSchema>;
export type RegisteredPatient = z.infer<typeof registeredPatientSchema>;
export type PatientDetail = z.infer<typeof patientDetailSchema>;
export type Report = z.infer<typeof reportSchema>;
export type ReportDetail = z.infer<typeof reportDetailSchema>;
export type ReportPatient = z.infer<typeof reportPatientsSchema>;
export type HealthInfo = z.infer<typeof patientHealthInfoSchema>;
export type commonPatientHealthInfo = z.infer<
    typeof commonPatientHealthInfoSchema
>;
export type Institution = z.infer<typeof institutionSchema>;
export type AiResult = z.infer<typeof aiResultSchema>;
