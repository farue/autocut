import { formatNumber } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Pipe({
  name: 'i18nCurrency',
})
export class I18nCurrencyPipe implements PipeTransform {
  constructor(private translateService: TranslateService) {}

  transform(value?: string | number | null, locale?: string): string | null {
    if (value == null || value === '' || value !== value) {
      return null;
    }

    if (typeof value === 'string') {
      value = Number(value);
    }
    locale = locale ?? this.translateService.currentLang;
    return formatNumber(value, locale, '1.2-2') + ' €';
  }
}
