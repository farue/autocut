import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { AutocutSharedModule } from 'app/shared/shared.module';
import { AutocutCoreModule } from 'app/core/core.module';
import { AutocutAppRoutingModule } from './app-routing.module';
import { AutocutHomeModule } from './home/home.module';
import { AutocutEntityModule } from './entities/entity.module';
import { AutocutAppSelfAdministrationModule } from 'app/self-administration';
import { AutocutAppTeamsModule } from 'app/teams';
import { InfoRoutingModule } from './info/info-routing.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ActiveMenuDirective } from './layouts/navbar/active-menu.directive';
import { ErrorComponent } from './layouts/error/error.component';
import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';

registerLocaleData(localeDe);

@NgModule({
  imports: [
    BrowserModule,
    AutocutSharedModule,
    AutocutCoreModule,
    AutocutHomeModule,
    AutocutAppSelfAdministrationModule,
    AutocutAppTeamsModule,
    InfoRoutingModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    AutocutEntityModule,
    AutocutAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, ActiveMenuDirective, FooterComponent],
  bootstrap: [MainComponent],
})
export class AutocutAppModule {}
