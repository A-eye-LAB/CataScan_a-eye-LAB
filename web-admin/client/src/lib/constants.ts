import dateUtil from '@/lib/utils/date';

export const DEFAULT_VALUES = {
    patient: {
        name: '',
        sex: 'male' as const,
        email: '',
        dateOfBirth: dateUtil.formatToYYYYMMDD(new Date()),
    },
    patientDetail: {
        age: 0,
        sex: 'male' as const,
        // dateOfBirth: (() => new Date().toString())(),
        dateOfBirth: dateUtil.formatToYYYYMMDD(new Date()),
        healthInfo: {
            bloodSugarLevel: 0,
            cataract: false,
            diabetes: false,
            diastolicBp: 0,
            dontKnow: false,
            hypertension: false,
            leftEyeVision: 0,
            rightEyeVision: 0,
            systolicBp: 0,
            visitDate: dateUtil.formatToYYYYMMDD(new Date()),
        },
        // scanResults: [],
        phoneNum: '',
    },
    healthInfo: {
        bloodSugarLevel: 0,
        cataract: false,
        diabetes: false,
        diastolicBp: 0,
        dontKnow: false,
        hypertension: false,
        leftEyeVision: 0,
        rightEyeVision: 0,
        systolicBp: 0,
        visitDate: dateUtil.formatToYYYYMMDD(new Date()),
    },
    account: {
        institutionName: '',
        username: '',
        password: '',
        confirmPassword: '',
    },
};

export const statusCode = {
    /**
     * Successful 2xx
     */
    // Reserved
    OK: 200,
    CREATED: 201,
    ACCEPTED: 202,
    NON_AUTHORITATIVE_INFORMATION: 203,
    NO_CONTENT: 204,
    RESET_CONTENT: 205,
    PARTIAL_CONTENT: 206,

    /**
     * Redirection 3xx
     */
    // Reserved
    MULTIPLE_CHOICES: 300,
    MOVED_PERMANENTLY: 301,
    FOUND: 302,
    SEE_OTHER: 303,
    NOT_MODIFIED: 304,
    USE_PROXY: 305,
    //    : 306, // (Unused now but reserved)
    TEMPORARY_REDIRECT: 307,

    /**
     * Client Error 4xx
     */
    // Reserved
    BAD_REQUEST: 400,
    UNAUTHORIZED: 401,
    PAYMENT_REQUIRED: 402,
    FORBIDDEN: 403,
    NOT_FOUND: 404,
    METHOD_NOT_ALLOWED: 405,
    NOT_ACCEPTABLE: 406,
    PROXY_AUTHENTICATION_REQUIRED: 407,
    REQUEST_TIME_OUT: 408,
    CONFLICT: 409,
    GONE: 410,
    LENGTH_REQUIRED: 411,
    PRE_CONDITION_FAILED: 412,
    REQUEST_ENTITY_TOO_LARGE: 413,
    REQUEST_URI_TOO_LONG: 414,
    UNSUPPORTED_MEDIA_TYPE: 415,
    REQUESTED_RANGE_NOT_SATISFIABLE: 416,
    EXPECTATION_FAILED: 417,
    // Custom

    /**
     * Server Error 5xx
     */
    // Reserved
    INTERNAL_SERVER_ERROR: 500,
    NOT_IMPLEMENTED: 501,
    BAD_GATEWAY: 502,
    SERVICE_UNAVAILABLE: 503,
    GATEWAY_TIMEOUT: 504,
    HTTP_VERSION_NOT_SUPPORTED: 505,
    // Custom
    PERMISSION_DENIED: 520,
};

export const ACCOUNT = {
    INSTITUTION_SUGGESTIONS: [
        'Vision Center',
        'Bright Vision Ins.',
        'Vision Action Ins',
    ],
    DIALOG_MODE: ['Add', 'Edit'] as const,
};

export const PATIENT = {
    DIALOG_MODE: ['Add', 'Edit'] as const,
    IS_DELETED_PROFILES_CHECKED: 0,
};
