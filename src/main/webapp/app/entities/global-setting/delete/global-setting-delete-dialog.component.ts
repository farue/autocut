import {Component} from '@angular/core';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

import {IGlobalSetting} from '../global-setting.model';
import {GlobalSettingService} from '../service/global-setting.service';

@Component({
  templateUrl: './global-setting-delete-dialog.component.html',
})
export class GlobalSettingDeleteDialogComponent {
  globalSetting?: IGlobalSetting;

  constructor(protected globalSettingService: GlobalSettingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.globalSettingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
