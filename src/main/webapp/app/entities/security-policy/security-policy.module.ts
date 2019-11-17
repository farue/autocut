import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { SecurityPolicyComponent } from './security-policy.component';
import { SecurityPolicyDetailComponent } from './security-policy-detail.component';
import { SecurityPolicyUpdateComponent } from './security-policy-update.component';
import {
  SecurityPolicyDeleteDialogComponent,
  SecurityPolicyDeletePopupComponent
} from './security-policy-delete-dialog.component';
import { securityPolicyPopupRoute, securityPolicyRoute } from './security-policy.route';

const ENTITY_STATES = [...securityPolicyRoute, ...securityPolicyPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    SecurityPolicyComponent,
    SecurityPolicyDetailComponent,
    SecurityPolicyUpdateComponent,
    SecurityPolicyDeleteDialogComponent,
    SecurityPolicyDeletePopupComponent
  ],
  entryComponents: [SecurityPolicyDeleteDialogComponent]
})
export class AutocutSecurityPolicyModule {}
