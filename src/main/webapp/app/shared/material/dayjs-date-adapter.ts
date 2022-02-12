import { Inject, InjectionToken, Optional } from '@angular/core';
import { DateAdapter, MAT_DATE_LOCALE } from '@angular/material/core';
import dayjs from 'dayjs/esm';
import utc from 'dayjs/esm/plugin/utc';
import localeData from 'dayjs/esm/plugin/localeData';
import localizedFormat from 'dayjs/esm/plugin/localizedFormat';
import customParseFormat from 'dayjs/esm/plugin/customParseFormat';

export interface DayJsDateAdapterOptions {
  /**
   * Turns the use of utc dates on or off.
   * Changing this will change how Angular Material components like DatePicker output dates.
   * {@default false}
   */
  useUtc?: boolean;
}

/** InjectionToken for Dayjs date adapter to configure options. */
export const MAT_DAYJS_DATE_ADAPTER_OPTIONS = new InjectionToken<DayJsDateAdapterOptions>('MAT_DAYJS_DATE_ADAPTER_OPTIONS', {
  providedIn: 'root',
  factory: MAT_DAYJS_DATE_ADAPTER_OPTIONS_FACTORY,
});

export function MAT_DAYJS_DATE_ADAPTER_OPTIONS_FACTORY(): DayJsDateAdapterOptions {
  return {
    useUtc: false,
  };
}

/** Creates an array and fills it with values. */
function range<T>(length: number, valueFunction: (index: number) => T): T[] {
  const valuesArray = Array(length);
  for (let i = 0; i < length; i++) {
    valuesArray[i] = valueFunction(i);
  }
  return valuesArray as T[];
}

/** Adapts Dayjs Dates for use with Angular Material. */
export class DayjsDateAdapter extends DateAdapter<dayjs.Dayjs> {
  private localeData!: {
    firstDayOfWeek: number;
    longMonths: string[];
    shortMonths: string[];
    dates: string[];
    longDaysOfWeek: string[];
    shortDaysOfWeek: string[];
    narrowDaysOfWeek: string[];
  };

  constructor(
    @Optional() @Inject(MAT_DATE_LOCALE) public dateLocale: string,
    @Optional() @Inject(MAT_DAYJS_DATE_ADAPTER_OPTIONS) private options?: DayJsDateAdapterOptions
  ) {
    super();

    this.initializeParser(dateLocale);
  }

  setLocale(locale: string): void {
    super.setLocale(locale);

    const dayJsLocaleData = this.dayJs().localeData();
    this.localeData = {
      firstDayOfWeek: dayJsLocaleData.firstDayOfWeek(),
      longMonths: dayJsLocaleData.months(),
      shortMonths: dayJsLocaleData.monthsShort(),
      dates: range(31, i => this.createDate(2017, 0, i + 1).format('D')),
      longDaysOfWeek: range(7, i => this.dayJs().set('day', i).format('dddd')),
      shortDaysOfWeek: dayJsLocaleData.weekdaysShort(),
      narrowDaysOfWeek: dayJsLocaleData.weekdaysMin(),
    };
  }

  getYear(date: dayjs.Dayjs): number {
    return this.dayJs(date).year();
  }

  getMonth(date: dayjs.Dayjs): number {
    return this.dayJs(date).month();
  }

  getDate(date: dayjs.Dayjs): number {
    return this.dayJs(date).date();
  }

  getDayOfWeek(date: dayjs.Dayjs): number {
    return this.dayJs(date).day();
  }

  getMonthNames(style: 'long' | 'short' | 'narrow'): string[] {
    return style === 'long' ? this.localeData.longMonths : this.localeData.shortMonths;
  }

  getDateNames(): string[] {
    return this.localeData.dates;
  }

  getDayOfWeekNames(style: 'long' | 'short' | 'narrow'): string[] {
    if (style === 'long') {
      return this.localeData.longDaysOfWeek;
    }
    if (style === 'short') {
      return this.localeData.shortDaysOfWeek;
    }
    return this.localeData.narrowDaysOfWeek;
  }

  getYearName(date: dayjs.Dayjs): string {
    return this.dayJs(date).format('YYYY');
  }

  getFirstDayOfWeek(): number {
    return this.localeData.firstDayOfWeek;
  }

  getNumDaysInMonth(date: dayjs.Dayjs): number {
    return this.dayJs(date).daysInMonth();
  }

  clone(date: dayjs.Dayjs): dayjs.Dayjs {
    return date.clone();
  }

  createDate(year: number, month: number, date: number): dayjs.Dayjs {
    const returnDayjs = this.dayJs().set('year', year).set('month', month).set('date', date);
    return returnDayjs;
  }

  today(): dayjs.Dayjs {
    return this.dayJs();
  }

  parse(value: any, parseFormat: string): dayjs.Dayjs | null {
    if (value && typeof value === 'string') {
      const longDateFormat = dayjs().localeData().longDateFormat(parseFormat); // MM/DD/YYY or DD-MM-YYYY, etc.

      let parsed = this.dayJs(value, longDateFormat, this.locale);
      if (parsed.isValid()) {
        // string value is exactly like long date format
        return parsed;
      }

      if (value.length === 8) {
        // user might have typed 24012020 or 01242020
        // strip long date format of non-alphabetic characters so we get MMDDYYYY or DDMMYYYY
        const format = longDateFormat.replace(/[\W_]+/g, '');
        parsed = this.dayJs(value, format, this.locale);
        if (parsed.isValid()) {
          return parsed;
        }
      }
      if (value.length > 2 && value.length < 6) {
        // user might have typed 01/24, 24-01, 1/24, 24/1 or 24-1
        // try to extract month and day part and parse them with custom format
        let parts: string[] = [];
        if (value.indexOf('/') !== -1) {
          parts = value.split('/');
        }
        if (value.indexOf('-') !== -1) {
          parts = value.split('-');
        }
        if (value.indexOf('.') !== -1) {
          parts = value.split('.');
        }
        if (parts.length === 2) {
          let dayPart: string;
          let monthPart: string;
          if (longDateFormat.startsWith('D')) {
            dayPart = parts[0];
            monthPart = parts[1];
          } else {
            monthPart = parts[0];
            dayPart = parts[1];
          }
          parsed = this.dayJs(dayPart + monthPart, 'DDMM', this.locale);
          if (parsed.isValid()) {
            return parsed;
          }
        }
      }
      if (value.length === 2) {
        // user might have typed 01, parse DD only
        const format = 'DD';
        parsed = this.dayJs(value, format, this.locale);
        if (parsed.isValid()) {
          return parsed;
        }
      }
      if (value.length === 1) {
        // user might have typed 1, parse D only
        const format = 'D';
        parsed = this.dayJs(value, format, this.locale);
        if (parsed.isValid()) {
          return parsed;
        }
      }

      // not able to parse anything sensible, return something invalid so input can be corrected
      return this.dayJs(null);
    }

    return value ? this.dayJs(value).locale(this.locale) : null;
  }

  format(date: dayjs.Dayjs, displayFormat: string): string {
    if (!this.isValid(date)) {
      throw Error('DayjsDateAdapter: Cannot format invalid date.');
    }
    return date.locale(this.locale).format(displayFormat);
  }

  addCalendarYears(date: dayjs.Dayjs, years: number): dayjs.Dayjs {
    return date.add(years, 'year');
  }

  addCalendarMonths(date: dayjs.Dayjs, months: number): dayjs.Dayjs {
    return date.add(months, 'month');
  }

  addCalendarDays(date: dayjs.Dayjs, days: number): dayjs.Dayjs {
    return date.add(days, 'day');
  }

  toIso8601(date: dayjs.Dayjs): string {
    return date.toISOString();
  }

  /**
   * Attempts to deserialize a value to a valid date object. This is different from parsing in that
   * deserialize should only accept non-ambiguous, locale-independent formats (e.g. a ISO 8601
   * string). The default implementation does not allow any deserialization, it simply checks that
   * the given value is already a valid date object or null. The `<mat-datepicker>` will call this
   * method on all of it's `@Input()` properties that accept dates. It is therefore possible to
   * support passing values from your backend directly to these properties by overriding this method
   * to also deserialize the format used by your backend.
   * @param value The value to be deserialized into a date object.
   * @returns The deserialized date object, either a valid date, null if the value can be
   *     deserialized into a null date (e.g. the empty string), or an invalid date.
   */
  deserialize(value: any): dayjs.Dayjs | null {
    let date: dayjs.Dayjs | string | undefined;
    if (value instanceof Date) {
      date = this.dayJs(value);
    } else if (this.isDateInstance(value)) {
      // Note: assumes that cloning also sets the correct locale.
      return this.clone(value);
    }
    if (typeof value === 'string') {
      if (!value) {
        return null;
      }
      date = this.dayJs(value).toISOString();
    }
    if (date && this.isValid(date)) {
      return this.dayJs(date); // NOTE: Is this necessary since Dayjs is immutable and Moment was not?
    }
    return super.deserialize(value);
  }

  isDateInstance(obj: any): boolean {
    return dayjs.isDayjs(obj);
  }

  isValid(date: dayjs.Dayjs | string): boolean {
    return this.dayJs(date).isValid();
  }

  invalid(): dayjs.Dayjs {
    return this.dayJs(null);
  }

  private initializeParser(dateLocale: string): void {
    if (this.shouldUseUtc) {
      dayjs.extend(utc);
    }

    dayjs.extend(localizedFormat);
    dayjs.extend(customParseFormat);
    dayjs.extend(localeData);

    this.setLocale(dateLocale);
  }

  private dayJs(input?: any, format?: string, locale?: string): dayjs.Dayjs {
    if (this.shouldUseUtc) {
      // TODO: Use strict mode (not yet supported): https://github.com/iamkun/dayjs/issues/1027
      return dayjs.utc(input, format);
      // return dayjs(input, { format, locale, utc: this.shouldUseUtc }, locale).utc();
    } else {
      return dayjs(input, format, locale, true);
      // return dayjs(input, { format, locale }, locale);
    }
  }

  private get shouldUseUtc(): boolean {
    const { useUtc }: DayJsDateAdapterOptions = this.options ?? {};
    return !!useUtc;
  }
}
