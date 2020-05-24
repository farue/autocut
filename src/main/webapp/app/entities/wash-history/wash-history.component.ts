import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IWashHistory } from 'app/shared/model/wash-history.model';
import { WashHistoryService } from './wash-history.service';
import { WashHistoryDeleteDialogComponent } from './wash-history-delete-dialog.component';

@Component({
  selector: 'jhi-wash-history',
  templateUrl: './wash-history.component.html',
})
export class WashHistoryComponent implements OnInit, OnDestroy {
  washHistories?: IWashHistory[];
  eventSubscriber?: Subscription;

  constructor(
    protected washHistoryService: WashHistoryService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.washHistoryService.query().subscribe((res: HttpResponse<IWashHistory[]>) => (this.washHistories = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInWashHistories();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IWashHistory): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInWashHistories(): void {
    this.eventSubscriber = this.eventManager.subscribe('washHistoryListModification', () => this.loadAll());
  }

  delete(washHistory: IWashHistory): void {
    const modalRef = this.modalService.open(WashHistoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.washHistory = washHistory;
  }
}
