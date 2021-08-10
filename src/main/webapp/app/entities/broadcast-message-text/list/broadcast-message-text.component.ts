import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBroadcastMessageText } from '../broadcast-message-text.model';
import { BroadcastMessageTextService } from '../service/broadcast-message-text.service';
import { BroadcastMessageTextDeleteDialogComponent } from '../delete/broadcast-message-text-delete-dialog.component';

@Component({
  selector: 'jhi-broadcast-message-text',
  templateUrl: './broadcast-message-text.component.html',
})
export class BroadcastMessageTextComponent implements OnInit {
  broadcastMessageTexts?: IBroadcastMessageText[];
  isLoading = false;

  constructor(protected broadcastMessageTextService: BroadcastMessageTextService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.broadcastMessageTextService.query().subscribe(
      (res: HttpResponse<IBroadcastMessageText[]>) => {
        this.isLoading = false;
        this.broadcastMessageTexts = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBroadcastMessageText): number {
    return item.id!;
  }

  delete(broadcastMessageText: IBroadcastMessageText): void {
    const modalRef = this.modalService.open(BroadcastMessageTextDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.broadcastMessageText = broadcastMessageText;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
