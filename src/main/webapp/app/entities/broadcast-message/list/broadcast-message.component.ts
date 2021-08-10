import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBroadcastMessage } from '../broadcast-message.model';
import { BroadcastMessageService } from '../service/broadcast-message.service';
import { BroadcastMessageDeleteDialogComponent } from '../delete/broadcast-message-delete-dialog.component';

@Component({
  selector: 'jhi-broadcast-message',
  templateUrl: './broadcast-message.component.html',
})
export class BroadcastMessageComponent implements OnInit {
  broadcastMessages?: IBroadcastMessage[];
  isLoading = false;

  constructor(protected broadcastMessageService: BroadcastMessageService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.broadcastMessageService.query().subscribe(
      (res: HttpResponse<IBroadcastMessage[]>) => {
        this.isLoading = false;
        this.broadcastMessages = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBroadcastMessage): number {
    return item.id!;
  }

  delete(broadcastMessage: IBroadcastMessage): void {
    const modalRef = this.modalService.open(BroadcastMessageDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.broadcastMessage = broadcastMessage;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
