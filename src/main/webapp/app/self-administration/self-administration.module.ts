import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from '../shared';

import { SELF_ADMINISTRATION_ROUTE, SelfAdministrationComponent } from './';
import { SpokesmanComponent } from './spokesman/spokesman.component';
import { AssignmentCommitteeComponent } from './assignment-committee/assignment-committee.component';
import { JanitorComponent } from './janitor/janitor.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forRoot([SELF_ADMINISTRATION_ROUTE], { useHash: true })],
  declarations: [SelfAdministrationComponent, SpokesmanComponent, AssignmentCommitteeComponent, JanitorComponent],
  entryComponents: [],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutAppSelfAdministrationModule {}
