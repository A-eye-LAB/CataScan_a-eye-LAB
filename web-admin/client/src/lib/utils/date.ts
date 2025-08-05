import { format  } from 'date-fns';
import { formatInTimeZone } from 'date-fns-tz';

const dateUtil = {
    formatToYYYYMMDD: (date: Date) => {
        return format(date, 'yyyy-MM-dd');
    },

    formatToLocalYYYYMMDD: (date: Date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        return `${year}-${month}-${day}`;
    },

   formatUTCToLocalString: (
        utcString: string,
        pattern = 'yyyy-MM-dd HH:mm:ss',
        timeZone?: string
      ) => {
        const tz = timeZone || Intl.DateTimeFormat().resolvedOptions().timeZone;

        const utcDate = new Date(utcString.includes('Z') ? utcString : utcString.replace(' ', 'T') + 'Z');
      
        return formatInTimeZone(utcDate, tz, pattern);
      },
};

export default dateUtil;
