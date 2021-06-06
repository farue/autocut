import * as dayjs from 'dayjs';

export function toDate(value: null): null;
export function toDate(value: undefined): undefined;
export function toDate(value: dayjs.Dayjs): dayjs.Dayjs;
export function toDate(value: dayjs.Dayjs | undefined): dayjs.Dayjs | undefined;
export function toDate(value: string | Date | dayjs.Dayjs): dayjs.Dayjs;
export function toDate(value: string | Date | dayjs.Dayjs | null | undefined): dayjs.Dayjs | null | undefined {
  if (value === null) {
    return null;
  }
  if (value === undefined) {
    return undefined;
  }
  return dayjs(value);
}
