import { getServerSession } from 'next-auth';
import { getSession } from 'next-auth/react';
import { statusCode } from '@/lib/constants';
import { authOptions } from '@/lib/utils/authOptions';
import apiUrl from './apiUrl';

const BASE_URL =
    typeof window === 'undefined' ? process.env.NEXT_PUBLIC_API_URL : '/api/v1';

const defaultHeaders: Record<string, string> = {
    'Content-Type': 'application/json',
};

const defaultConfig = {
    method: 'GET',
    headers: defaultHeaders,
    timeout: 5000,
};

export const validateStatus = (code: number): boolean => {
    switch (code) {
        // Successful 2xx
        case statusCode.OK:
        case statusCode.CREATED:
        case statusCode.ACCEPTED:
        case statusCode.NON_AUTHORITATIVE_INFORMATION:
        case statusCode.NO_CONTENT:
        case statusCode.RESET_CONTENT:
        case statusCode.PARTIAL_CONTENT:
            return true;
        default:
            return false;
    }
};

const extractEssentialDataFromResponse = async <T>(
    response: Response
): Promise<{ status: number; data: T }> => {
    const contentType = response.headers.get('Content-Type');

    let data;
    if (contentType?.includes('application/json')) {
        data = await response.json();
    } else if (contentType?.includes('text')) {
        data = await response.text(); // ✅ 텍스트 처리 추가
    } else {
        data = null;
    }

    return { status: response.status, data };
};

export const paramsSerializer = (params: object) => {
    return Object.entries(params)
        .map(([key, value]) => {
            if (Array.isArray(value)) {
                return value.map((subValue) => `${key}=${subValue}`).join('&');
            }
            return `${key}=${value}`;
        })
        .join('&');
};

export const isFetchError = (error: unknown): boolean => {
    return error instanceof Error;
};

const canSkipSession = (url: string) => {
    const skipSessionUrls = [
        apiUrl.getAuthLoginUrl(),
        apiUrl.getAuthRefreshUrl(),
    ];

    return skipSessionUrls.includes(url);
};

const getAuthHeaders = async (url: string) => {
    const headers = { ...defaultHeaders };

    if (canSkipSession(url)) {
        return headers;
    }
    if (typeof window === 'undefined') {
        const session = await getServerSession(authOptions);

        const token = session?.user?.token;

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
    } else {
        const session = await getSession();

        if (session?.user.token) {
            const { token } = session.user;

            headers['Authorization'] = `Bearer ${token}`;
        }
    }

    return headers;
};

const restClient = {
    get: async <T>(url: string, params = {}, config = {}) => {
        const queryParams = paramsSerializer(params);
        const finalUrl = queryParams
            ? `${BASE_URL}${url}?${queryParams}`
            : `${BASE_URL}${url}`;

        const authHeaders = await getAuthHeaders(url);
        const response = await fetch(finalUrl, {
            ...defaultConfig,
            method: 'GET',
            headers: authHeaders,
            ...config,
        });

        if (!validateStatus(response.status)) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        return extractEssentialDataFromResponse<T>(response);
    },

    post: async <T>(url: string, params = {}, config = {}) => {
        const authHeaders = await getAuthHeaders(url);
        const response = await fetch(`${BASE_URL}${url}`, {
            ...defaultConfig,
            method: 'POST',
            headers: authHeaders,
            body: JSON.stringify(params),
            ...config,
        });

        if (!validateStatus(response.status)) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        return extractEssentialDataFromResponse<T>(response);
    },

    put: async <T>(url: string, params = {}, config = {}) => {
        const authHeaders = await getAuthHeaders(url);
        const response = await fetch(`${BASE_URL}${url}`, {
            ...defaultConfig,
            method: 'PUT',
            body: JSON.stringify(params),
            headers: authHeaders,
            ...config,
        });

        if (!validateStatus(response.status)) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        return extractEssentialDataFromResponse<T>(response);
    },

    patch: async <T>(url: string, params = {}, config = {}) => {
        const authHeaders = await getAuthHeaders(url);
        const response = await fetch(`${BASE_URL}${url}`, {
            ...defaultConfig,
            method: 'PATCH',
            headers: authHeaders,
            body: JSON.stringify(params),
            ...config,
        });

        if (!validateStatus(response.status)) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        return extractEssentialDataFromResponse<T>(response);
    },

    delete: async <T>(url: string, params = {}) => {
        const authHeaders = await getAuthHeaders(url);
        const response = await fetch(`${BASE_URL}${url}`, {
            ...defaultConfig,
            method: 'DELETE',
            headers: authHeaders,
            body: JSON.stringify(params),
        });

        if (!validateStatus(response.status)) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        return extractEssentialDataFromResponse<T>(response);
    },
};

export default restClient;
