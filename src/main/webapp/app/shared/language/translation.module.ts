import { NgModule } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MissingTranslationHandler, TranslateLoader, TranslateModule, TranslateService } from '@ngx-translate/core';
import { missingTranslationHandler, translatePartialLoader } from 'app/config/translation.config';
import { SessionStorageService } from 'ngx-webstorage';
import { DateAdapter } from '@angular/material/core';
import dayjs from 'dayjs/esm';

@NgModule({
  imports: [
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: translatePartialLoader,
        deps: [HttpClient],
      },
      missingTranslationHandler: {
        provide: MissingTranslationHandler,
        useFactory: missingTranslationHandler,
      },
    }),
  ],
})
export class TranslationModule {
  constructor(
    private translateService: TranslateService,
    sessionStorageService: SessionStorageService,
    dateAdapter: DateAdapter<dayjs.Dayjs>
  ) {
    translateService.setDefaultLang('de');
    // if user have changed language and navigates away from the application and back to the application then use previously choosed language
    const langKey = sessionStorageService.retrieve('locale') ?? 'de';
    translateService.use(langKey);

    // Autocut: Set material locale
    dateAdapter.setLocale(langKey);
  }
}
