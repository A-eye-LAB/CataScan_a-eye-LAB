import React from 'react';
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import { TDialogProps } from '@/lib/types/ui';

type TDeleteDialogProps = TDialogProps & {
    title: string;
    onCancel?: () => void;
    onDelete: () => void;
};

function DeleteDialog(props: TDeleteDialogProps) {
    const { isOpenDialog, setIsOpenDialog, title, onCancel, onDelete } = props;

    return (
        <AlertDialog open={isOpenDialog} onOpenChange={setIsOpenDialog}>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle
                        className={'text-CATASCAN-text-basic-foreground'}>
                        {title}
                    </AlertDialogTitle>
                    <AlertDialogDescription>
                        <span
                            className={
                                'whitespace-break-spaces text-CATASCAN-text-basic-foreground'
                            }>
                            {
                                'This action cannot be undone.\nDo you want to proceed?'
                            }
                        </span>
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel onClick={onCancel}>
                        {'Cancel'}
                    </AlertDialogCancel>
                    <AlertDialogAction onClick={onDelete} variant={'catascan'}>
                        {'Delete'}
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export default DeleteDialog;
