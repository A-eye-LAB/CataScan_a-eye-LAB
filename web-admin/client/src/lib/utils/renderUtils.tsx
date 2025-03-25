import { z } from 'zod';
import { Badge } from '@/components/ui/badge';
import { aiResultSchema } from '@/lib/types/schema';

const renderUtil = {
    renderAiResultBadge: (
        aiValue: z.infer<typeof aiResultSchema> | string,
        className?: string
    ) => {
        if (aiValue === 'lowRisk') {
            return (
                <Badge
                    className={`rounded-full ${className ?? ''}`}
                    variant={'catascanBlue'}>
                    Low Risk
                </Badge>
            );
        } else if (aiValue === 'unknown') {
            return (
                <Badge
                    className={`rounded-full ${className ?? ''}`}
                    variant={'catascanGrey'}>
                    Ungradable
                </Badge>
            );
        } else {
            return (
                <Badge
                    className={`rounded-full ${className ?? ''}`}
                    variant={'catascanRed'}>
                    Requires Attention
                </Badge>
            );
        }
    },
};

export default renderUtil;
