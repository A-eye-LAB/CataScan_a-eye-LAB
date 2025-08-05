'use client';

import React from 'react';
import { useRouter } from 'next/navigation';
import { CircleUserRoundIcon, FileTextIcon } from 'lucide-react';
import { useIsMobile } from '@/hooks/use-mobile';
import {
    AppSidebarMenuItem,
    AppSidebarMenuWithCollapsible,
} from '@/components/common/app-sidebar/sidebar-menu-item';
import {
    Sidebar,
    SidebarContent,
    SidebarGroup,
    SidebarGroupContent,
    SidebarHeader,
    SidebarMenu,
} from '@/components/ui/sidebar';
import { EyeLogoIcon, TextLogoIcon } from '@/components/ui/icon';
import {
    TAppSidebarMenuItem,
    TAppSidebarMenuWithCollapsible,
} from '@/lib/types/ui';

const SIDEBAR_ITEMS: (TAppSidebarMenuItem | TAppSidebarMenuWithCollapsible)[] =
    [
        {
            title: 'Reports',
            url: '/reports',
            icon: FileTextIcon,
        },
        {
            title: 'Patient Profiles',
            url: '/patients',
            icon: CircleUserRoundIcon,
        },
        // {
        //     title: 'Statistics',
        //     url: '/statistics',
        //     icon: ChartNoAxesColumnIncreasingIcon,
        // },
        {
            title: 'User Accounts',
            url: '/accounts',
            icon: CircleUserRoundIcon,
        },
    ];

function AppSidebar() {
    const router = useRouter();
    const isMobile = useIsMobile();

    return (
        <Sidebar>
            <SidebarHeader
                className={'bg-background py-4 px-3 hover:cursor-pointer'}
                onClick={() => {
                    router.push('/reports');
                }}>
                {isMobile ? (
                    <div className={'flex self-center'}>
                        <EyeLogoIcon />
                    </div>
                ) : (
                    <div className={'flex items-center'}>
                        <EyeLogoIcon />
                        <TextLogoIcon />
                    </div>
                )}
            </SidebarHeader>
            <SidebarContent className={'bg-background'}>
                <SidebarGroup className={'px-2.5'}>
                    <SidebarGroupContent>
                        <SidebarMenu>
                            {SIDEBAR_ITEMS.map((item) => {
                                if ('subItems' in item) {
                                    return (
                                        <AppSidebarMenuWithCollapsible
                                            key={item.title}
                                            title={item.title}
                                            subItems={item.subItems}
                                        />
                                    );
                                }
                                return (
                                    <AppSidebarMenuItem
                                        key={item.title}
                                        title={item.title}
                                        icon={item.icon}
                                        url={item.url}
                                    />
                                );
                            })}
                        </SidebarMenu>
                    </SidebarGroupContent>
                </SidebarGroup>
            </SidebarContent>
        </Sidebar>
    );
}

export default AppSidebar;
