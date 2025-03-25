'use client';
import React from 'react';
import Link from 'next/link';
import { ChevronDown } from 'lucide-react';
import { usePathname } from 'next/navigation';
import { useIsMobile } from '@/hooks/use-mobile';
import {
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarMenuSub,
    SidebarMenuSubItem,
} from '@/components/ui/sidebar';
import {
    Collapsible,
    CollapsibleContent,
    CollapsibleTrigger,
} from '@/components/ui/collapsible';
import {
    TAppSidebarMenuItem,
    TAppSidebarMenuWithCollapsible,
} from '@/lib/types/ui';

type TAppSidebarMenuItemProps = TAppSidebarMenuItem;
type AppSidebarMenuWithCollapsibleProps = TAppSidebarMenuWithCollapsible;

function AppSidebarMenuItem(props: TAppSidebarMenuItemProps) {
    const isMobile = useIsMobile();
    const pathname = usePathname();

    const renderSidebarMenuItemContents = (item: TAppSidebarMenuItemProps) => {
        return item.url ? (
            <Link href={item.url}>
                {item.icon && <item.icon />}
                {!isMobile && <span>{item.title}</span>}
            </Link>
        ) : (
            <>
                {item.icon && <item.icon />}
                {!isMobile && <span>{item.title}</span>}
            </>
        );
    };

    return (
        <SidebarMenuItem>
            <SidebarMenuButton
                isActive={pathname.includes(props?.url as string)}
                className={isMobile ? 'justify-center' : ''}
                asChild>
                {renderSidebarMenuItemContents({ ...props })}
            </SidebarMenuButton>
        </SidebarMenuItem>
    );
}

function AppSidebarMenuWithCollapsible(
    props: AppSidebarMenuWithCollapsibleProps
) {
    const { title, icon: Icon, subItems } = props;

    const renderSidebarMenuItemContents = (item: TAppSidebarMenuItem) => {
        return item.url ? (
            <Link href={item.url}>
                {item.icon && <item.icon />}
                <span>{item.title}</span>
            </Link>
        ) : (
            <>
                {item.icon && <item.icon />}
                <span>{item.title}</span>
            </>
        );
    };

    return (
        <SidebarMenu>
            <Collapsible defaultOpen>
                <SidebarMenuItem>
                    <CollapsibleTrigger asChild>
                        <SidebarMenuButton>
                            {Icon && <Icon />}
                            {title}
                            <ChevronDown className="ml-auto group-data-[state=open]/collapsible:rotate-180" />
                        </SidebarMenuButton>
                    </CollapsibleTrigger>
                    <CollapsibleContent>
                        {subItems.map((subItem) => (
                            <SidebarMenuSub key={subItem.title}>
                                <SidebarMenuSubItem>
                                    <SidebarMenuButton asChild>
                                        {renderSidebarMenuItemContents(subItem)}
                                    </SidebarMenuButton>
                                </SidebarMenuSubItem>
                            </SidebarMenuSub>
                        ))}
                    </CollapsibleContent>
                </SidebarMenuItem>
            </Collapsible>
        </SidebarMenu>
    );
}

export { AppSidebarMenuItem, AppSidebarMenuWithCollapsible };
