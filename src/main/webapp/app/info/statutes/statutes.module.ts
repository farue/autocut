import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';

import { STATUTES_ROUTE, StatutesComponent } from './';
import { GeneralComponent } from 'app/info/statutes/general/general.component';
import { NetworkComponent } from 'app/info/statutes/network/network.component';
import { WashingComponent } from 'app/info/statutes/washing/washing.component';
import { ToolsComponent } from 'app/info/statutes/tools/tools.component';
import { AssignmentComponent } from 'app/info/statutes/assignment/assignment.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([STATUTES_ROUTE])],
  declarations: [StatutesComponent, GeneralComponent, NetworkComponent, AssignmentComponent, WashingComponent, ToolsComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class StatutesModule {}
