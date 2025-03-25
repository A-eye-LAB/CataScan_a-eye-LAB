import apiUrl from '@/lib/api/apiUrl';

const imageUtil = {
    getInternalImageUrl: (imageUrl: string) => {
        return `${apiUrl.getApiImageUrl()}?path=${encodeURIComponent(imageUrl)}`;
    },
};

export default imageUtil;
