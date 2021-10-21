import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {GlobalSettingComponent} from './list/global-setting.component';
import {GlobalSettingDetailComponent} from './detail/global-setting-detail.component';
import {GlobalSettingUpdateComponent} from './update/global-setting-update.component';
import {GlobalSettingDeleteDialogComponent} from './delete/global-setting-delete-dialog.component';
import {GlobalSettingRoutingModule} from './route/global-setting-routing.module';

@NgModule({
  imports: [SharedModule, GlobalSettingRoutingModule],
  declarations: [GlobalSettingComponent, GlobalSettingDetailComponent, GlobalSettingUpdateComponent, GlobalSettingDeleteDialogComponent],
  entryComponents: [GlobalSettingDeleteDialogComponent],
})
export class GlobalSettingModule {}
