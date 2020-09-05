import { Pipe, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { JhiLanguageService } from 'ng-jhipster';

@Pipe({
  name: 'money',
  pure: false, // marked as impure to be triggered even if template parameters have not changed
})
export class MoneyPipe extends DecimalPipe implements PipeTransform {
  constructor(private jhiLanguageService: JhiLanguageService) {
    super(jhiLanguageService.getCurrentLanguage());
  }

  transform(value: any): string | null {
    return super.transform(value, '1.2-2', this.jhiLanguageService.getCurrentLanguage()) + ' €';
  }
}
