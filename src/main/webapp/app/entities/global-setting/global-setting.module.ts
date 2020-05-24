import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { GlobalSettingComponent } from './global-setting.component';
import { GlobalSettingDetailComponent } from './global-setting-detail.component';
import { GlobalSettingUpdateComponent } from './global-setting-update.component';
import { GlobalSettingDeleteDialogComponent } from './global-setting-delete-dialog.component';
import { globalSettingRoute } from './global-setting.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(globalSettingRoute)],
  declarations: [GlobalSettingComponent, GlobalSettingDetailComponent, GlobalSettingUpdateComponent, GlobalSettingDeleteDialogComponent],
  entryComponents: [GlobalSettingDeleteDialogComponent],
})
export class AutocutGlobalSettingModule {}
