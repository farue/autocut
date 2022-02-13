import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IWashHistory } from '../wash-history.model';
import { WashHistoryService } from '../service/wash-history.service';
import { WashHistoryDeleteDialogComponent } from '../delete/wash-history-delete-dialog.component';

@Component({
  selector: 'jhi-wash-history',
  templateUrl: './wash-history.component.html',
})
export class WashHistoryComponent implements OnInit {
  washHistories?: IWashHistory[];
  isLoading = false;

  constructor(protected washHistoryService: WashHistoryService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.washHistoryService.query().subscribe({
      next: (res: HttpResponse<IWashHistory[]>) => {
        this.isLoading = false;
        this.washHistories = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IWashHistory): number {
    return item.id!;
  }

  delete(washHistory: IWashHistory): void {
    const modalRef = this.modalService.open(WashHistoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.washHistory = washHistory;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
