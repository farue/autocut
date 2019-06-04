import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AutocutSharedModule} from 'app/shared';
import {GalleriaModule} from 'primeng/primeng';
import {HOME_ROUTE, HomeComponent} from './';
import {LightboxModule} from 'ngx-lightbox';

@NgModule({
  imports: [AutocutSharedModule, GalleriaModule, RouterModule.forChild([HOME_ROUTE]), LightboxModule],
  declarations: [HomeComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutHomeModule {}
