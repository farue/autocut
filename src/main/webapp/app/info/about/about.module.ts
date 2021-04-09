import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';

import { ABOUT_ROUTE } from 'app/info/about/about.route';
import { TermsComponent } from 'app/info/about/terms/terms.component';
import { PrivacyComponent } from 'app/info/about/privacy/privacy.component';
import { CookiesComponent } from 'app/info/about/cookies/cookies.component';
import { ImprintComponent } from 'app/info/about/imprint/imprint.component';
import { AboutComponent } from 'app/info/about/about.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([ABOUT_ROUTE])],
  declarations: [AboutComponent, TermsComponent, PrivacyComponent, CookiesComponent, ImprintComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AboutModule {}
