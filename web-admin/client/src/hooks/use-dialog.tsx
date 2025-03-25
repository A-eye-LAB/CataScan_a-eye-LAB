import { useCallback, useState } from 'react';

function useDialog(initialValue: boolean) {
    const [isOpen, setIsOpen] = useState(initialValue);

    const open = useCallback(() => {
        setIsOpen(true);
    }, []);

    const close = useCallback(() => {
        setIsOpen(false);
    }, []);

    const toggle = useCallback(() => {
        setIsOpen(!isOpen);
    }, [isOpen]);

    return {
        isOpen,
        setIsOpen,
        open,
        close,
        toggle,
    };
}

export default useDialog;
