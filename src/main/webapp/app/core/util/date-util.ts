import dayjs from 'dayjs/esm';

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

export function dateToString(value: null): null;
export function dateToString(value: undefined): undefined;
export function dateToString(value: string | Date | dayjs.Dayjs): string;
export function dateToString(value: string | Date | dayjs.Dayjs | null | undefined): string | null | undefined {
  if (value === null) {
    return null;
  }
  if (value === undefined) {
    return undefined;
  }
  if (typeof value === 'string') {
    return value;
  }
  return dayjs(value).toISOString();
}
