'use client';

import React from 'react';
import { MoreHorizontal } from 'lucide-react';
import { Button } from '@/components/ui/button';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

interface IModifyActionDropdownMenuProps {
    setIsOpenEditDialog: React.Dispatch<React.SetStateAction<boolean>>;
    setIsOpenDeleteDialog: React.Dispatch<React.SetStateAction<boolean>>;
    isEditRender?: boolean;
    onEdit?: () => void;
    onDelete?: () => void;
}

function ModifyActionDropdownMenu(props: IModifyActionDropdownMenuProps) {
    const {
        setIsOpenEditDialog,
        setIsOpenDeleteDialog,
        isEditRender = true,
        onEdit,
        onDelete,
    } = props;

    return (
        <>
            <DropdownMenu modal={false}>
                <DropdownMenuTrigger asChild>
                    <Button
                        variant="ghost"
                        className="flex h-8 w-8 p-0 data-[state=open]:bg-muted"
                        onClick={(event) => {
                            event.stopPropagation();
                        }}>
                        <MoreHorizontal />
                        <span className="sr-only">Open menu</span>
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-[160px]">
                    {isEditRender && (
                        <DropdownMenuItem
                            onClick={() => {
                                setIsOpenEditDialog(true);
                                if (onEdit) {
                                    onEdit();
                                }
                            }}>
                            Edit
                        </DropdownMenuItem>
                    )}
                    <DropdownMenuItem
                        onClick={() => {
                            setIsOpenDeleteDialog(true);
                            if (onDelete) {
                                onDelete();
                            }
                        }}>
                        Delete
                    </DropdownMenuItem>
                </DropdownMenuContent>
            </DropdownMenu>
        </>
    );
}

export default ModifyActionDropdownMenu;
