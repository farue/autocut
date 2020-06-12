import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { GalleriaModule } from 'primeng/galleria';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { LightboxModule } from 'ngx-lightbox';

@NgModule({
  imports: [AutocutSharedModule, GalleriaModule, LightboxModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent],
  providers: [LightboxModule],
})
export class AutocutHomeModule {}
