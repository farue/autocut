import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as dayjs from 'dayjs';

@Pipe({
  name: 'i18nDate',
  pure: false,
})
export class I18nDatePipe implements PipeTransform {
  constructor(private translateService: TranslateService) {}

  transform(value: dayjs.Dayjs | string | number | null | undefined, locale?: string, format?: string): string | null {
    if (value == null || value === '' || value !== value) {
      return null;
    }

    locale = locale ?? this.translateService.currentLang;
    if (!format) {
      if (locale.startsWith('de')) {
        format = 'L';
      } else {
        format = 'll';
      }
    }
    return dayjs(value, { locale }).format(format);
  }
}
