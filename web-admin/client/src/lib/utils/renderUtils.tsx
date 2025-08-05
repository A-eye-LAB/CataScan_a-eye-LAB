import { Badge } from '@/components/ui/badge';
import { AiResult } from '@/lib/types/schema';

const renderUtil = {
    renderAiResultBadge: (aiValue: AiResult, className?: string) => {
        if (aiValue === 'lowRisk') {
            return (
                <Badge
                    className={`rounded-full ${className ?? ''}`}
                    variant={'catascanBlue'}>
                    Low Risk
                </Badge>
            );
        } else if (aiValue === 'ungradable') {
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
