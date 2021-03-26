import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGlobalSetting } from '../global-setting.model';
import { GlobalSettingService } from '../service/global-setting.service';
import { GlobalSettingDeleteDialogComponent } from '../delete/global-setting-delete-dialog.component';

@Component({
  selector: 'jhi-global-setting',
  templateUrl: './global-setting.component.html',
})
export class GlobalSettingComponent implements OnInit {
  globalSettings?: IGlobalSetting[];
  isLoading = false;

  constructor(protected globalSettingService: GlobalSettingService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.globalSettingService.query().subscribe(
      (res: HttpResponse<IGlobalSetting[]>) => {
        this.isLoading = false;
        this.globalSettings = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IGlobalSetting): number {
    return item.id!;
  }

  delete(globalSetting: IGlobalSetting): void {
    const modalRef = this.modalService.open(GlobalSettingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.globalSetting = globalSetting;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
