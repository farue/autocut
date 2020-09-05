import { NgModule } from '@angular/core';
import { AutocutSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { AlertComponent } from './alert/alert.component';
import { AlertErrorComponent } from './alert/alert-error.component';
import { LoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { LocalizedDatePipe } from 'app/shared/pipes/localized-date.pipe';
import { MoneyPipe } from 'app/shared/pipes/money.pipe';

@NgModule({
  imports: [AutocutSharedLibsModule],
  declarations: [
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    LocalizedDatePipe,
    MoneyPipe,
  ],
  entryComponents: [LoginModalComponent],
  exports: [
    AutocutSharedLibsModule,
    FindLanguageFromKeyPipe,
    AlertComponent,
    AlertErrorComponent,
    LoginModalComponent,
    HasAnyAuthorityDirective,
    LocalizedDatePipe,
    MoneyPipe,
  ],
})
export class AutocutSharedModule {}
