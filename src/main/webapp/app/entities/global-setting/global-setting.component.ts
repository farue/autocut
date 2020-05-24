import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGlobalSetting } from 'app/shared/model/global-setting.model';
import { GlobalSettingService } from './global-setting.service';
import { GlobalSettingDeleteDialogComponent } from './global-setting-delete-dialog.component';

@Component({
  selector: 'jhi-global-setting',
  templateUrl: './global-setting.component.html',
})
export class GlobalSettingComponent implements OnInit, OnDestroy {
  globalSettings?: IGlobalSetting[];
  eventSubscriber?: Subscription;

  constructor(
    protected globalSettingService: GlobalSettingService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.globalSettingService.query().subscribe((res: HttpResponse<IGlobalSetting[]>) => (this.globalSettings = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInGlobalSettings();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IGlobalSetting): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInGlobalSettings(): void {
    this.eventSubscriber = this.eventManager.subscribe('globalSettingListModification', () => this.loadAll());
  }

  delete(globalSetting: IGlobalSetting): void {
    const modalRef = this.modalService.open(GlobalSettingDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.globalSetting = globalSetting;
  }
}
