import restClient from '@/lib/api/restClient';

async function fetcher<T>(url: string): Promise<T>;
async function fetcher<T>(params: [string, object]): Promise<T>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
async function fetcher<T>(params: any) {
    let response;
    if (typeof params === 'string') {
        response = await restClient.get<T>(params);
    } else {
        response = await restClient.get<T>(params[0], params[1]);
    }

    return response.data;
}

export default fetcher;
