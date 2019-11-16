import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {AutocutSharedModule} from 'app/shared/shared.module';

import {AuditsComponent} from './audits.component';

import {auditsRoute} from './audits.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([auditsRoute])],
  declarations: [AuditsComponent]
})
export class AuditsModule {
}
