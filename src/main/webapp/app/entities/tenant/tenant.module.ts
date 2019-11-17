import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { TenantComponent } from './tenant.component';
import { TenantDetailComponent } from './tenant-detail.component';
import { TenantUpdateComponent } from './tenant-update.component';
import { TenantDeleteDialogComponent, TenantDeletePopupComponent } from './tenant-delete-dialog.component';
import { tenantPopupRoute, tenantRoute } from './tenant.route';

const ENTITY_STATES = [...tenantRoute, ...tenantPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [TenantComponent, TenantDetailComponent, TenantUpdateComponent, TenantDeleteDialogComponent, TenantDeletePopupComponent],
  entryComponents: [TenantDeleteDialogComponent]
})
export class AutocutTenantModule {}
