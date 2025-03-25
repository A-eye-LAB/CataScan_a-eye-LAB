import dynamic from 'next/dynamic';

export const TextLogoIcon = dynamic(
    () => import('@/components/ui/icon/TextLogo')
);
export const EyeLogoIcon = dynamic(
    () => import('@/components/ui/icon/EyeLogo')
);

export type IconProps = {
    width?: number;
    height?: number;
    color?: string;
};
