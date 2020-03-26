import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TotpComponent } from './totp.component';
import { TotpDetailComponent } from './totp-detail.component';
import { TotpUpdateComponent } from './totp-update.component';
import { TotpDeleteDialogComponent } from './totp-delete-dialog.component';
import { totpRoute } from './totp.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(totpRoute)],
  declarations: [TotpComponent, TotpDetailComponent, TotpUpdateComponent, TotpDeleteDialogComponent],
  entryComponents: [TotpDeleteDialogComponent]
})
export class AutocutTotpModule {}
