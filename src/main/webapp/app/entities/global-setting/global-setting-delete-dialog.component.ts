import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IGlobalSetting } from 'app/shared/model/global-setting.model';
import { GlobalSettingService } from './global-setting.service';

@Component({
  templateUrl: './global-setting-delete-dialog.component.html'
})
export class GlobalSettingDeleteDialogComponent {
  globalSetting?: IGlobalSetting;

  constructor(
    protected globalSettingService: GlobalSettingService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.globalSettingService.delete(id).subscribe(() => {
      this.eventManager.broadcast('globalSettingListModification');
      this.activeModal.close();
    });
  }
}
