import { Pipe, PipeTransform } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';

@Pipe({
  name: 'i18n',
  pure: false,
})
export class I18nPipe extends TranslatePipe implements PipeTransform {
  transform(value?: string | null, ...args: any[]): string | null {
    if (value == null || value === '' || value !== value) {
      return null;
    }

    return value.replace(/i18n{([^}]+)}/g, (match, token) => super.transform(token, args) as string);
  }
}
