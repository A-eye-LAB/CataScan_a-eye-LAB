import { format } from 'date-fns';

const dateUtil = {
    formatToYYYYMMDD: (date: Date) => {
        return format(date, 'yyyy-MM-dd');
    },
};

export default dateUtil;
