import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { SecurityPolicyComponent } from './security-policy.component';
import { SecurityPolicyDetailComponent } from './security-policy-detail.component';
import { SecurityPolicyUpdateComponent } from './security-policy-update.component';
import { SecurityPolicyDeleteDialogComponent } from './security-policy-delete-dialog.component';
import { securityPolicyRoute } from './security-policy.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(securityPolicyRoute)],
  declarations: [
    SecurityPolicyComponent,
    SecurityPolicyDetailComponent,
    SecurityPolicyUpdateComponent,
    SecurityPolicyDeleteDialogComponent
  ],
  entryComponents: [SecurityPolicyDeleteDialogComponent]
})
export class AutocutSecurityPolicyModule {}
