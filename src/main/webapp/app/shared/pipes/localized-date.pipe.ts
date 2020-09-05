import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';

@Pipe({
  name: 'localizedDate',
  pure: false, // marked as impure to be triggered even if template parameters have not changed
})
export class LocalizedDatePipe extends DatePipe implements PipeTransform {
  constructor(private jhiLanguageService: JhiLanguageService) {
    super(jhiLanguageService.getCurrentLanguage());
  }

  transform(value: any, format?: string): string | null {
    return super.transform(value, format, undefined, this.jhiLanguageService.getCurrentLanguage());
  }
}
