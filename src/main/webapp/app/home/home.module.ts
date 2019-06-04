import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared';
import {GalleriaModule} from 'primeng/primeng';
import { HOME_ROUTE, HomeComponent } from './';

@NgModule({
  imports: [AutocutSharedModule, GalleriaModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutHomeModule {}
