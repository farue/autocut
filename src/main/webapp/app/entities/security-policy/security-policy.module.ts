import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {SecurityPolicyComponent} from './list/security-policy.component';
import {SecurityPolicyDetailComponent} from './detail/security-policy-detail.component';
import {SecurityPolicyUpdateComponent} from './update/security-policy-update.component';
import {SecurityPolicyDeleteDialogComponent} from './delete/security-policy-delete-dialog.component';
import {SecurityPolicyRoutingModule} from './route/security-policy-routing.module';

@NgModule({
  imports: [SharedModule, SecurityPolicyRoutingModule],
  declarations: [
    SecurityPolicyComponent,
    SecurityPolicyDetailComponent,
    SecurityPolicyUpdateComponent,
    SecurityPolicyDeleteDialogComponent,
  ],
  entryComponents: [SecurityPolicyDeleteDialogComponent],
})
export class SecurityPolicyModule {}
