import React, { MouseEventHandler } from 'react';
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

type TBaseAlertDialogProps = TDialogProps & {
    title: string;
    description?: string | React.ReactNode;
    onCancel?: () => void;
    onConfirm: MouseEventHandler<HTMLButtonElement>;
    cancelButtonText?: string;
    confirmButtonText?: string;
};

function BaseAlertDialog(props: TBaseAlertDialogProps) {
    const {
        isOpenDialog,
        setIsOpenDialog,
        title,
        description,
        onCancel,
        onConfirm,
        cancelButtonText = 'Cancel',
        confirmButtonText = 'Confirm',
    } = props;

    return (
        <AlertDialog open={isOpenDialog} onOpenChange={setIsOpenDialog}>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle
                        className={'text-CATASCAN-text-basic-foreground'}>
                        {title}
                    </AlertDialogTitle>
                    <AlertDialogDescription>
                        {typeof description === 'string' ? (
                            <span
                                className={
                                    'whitespace-break-spaces text-CATASCAN-text-basic-foreground'
                                }>
                                {description}
                            </span>
                        ) : (
                            <>{description}</>
                        )}
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel onClick={onCancel}>
                        {cancelButtonText}
                    </AlertDialogCancel>
                    <AlertDialogAction onClick={onConfirm} variant={'catascan'}>
                        {confirmButtonText}
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export default BaseAlertDialog;
