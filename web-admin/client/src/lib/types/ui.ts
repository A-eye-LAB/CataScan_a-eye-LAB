import React, {ComponentType} from 'react'; // Sidebar

// Sidebar
export type TAppSidebarMenuItem = {
    title: string;
    url?: string;
    icon?: ComponentType<{ className?: string }>;
};

export type TAppSidebarMenuWithCollapsible = TAppSidebarMenuItem & {
    subItems: Array<TAppSidebarMenuItem>;
};

// Dialog
export type TDialogProps = {
    isOpenDialog: boolean;
    setIsOpenDialog: React.Dispatch<React.SetStateAction<boolean>>;
};
