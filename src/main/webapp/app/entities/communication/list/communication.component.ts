import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommunication } from '../communication.model';
import { CommunicationService } from '../service/communication.service';
import { CommunicationDeleteDialogComponent } from '../delete/communication-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-communication',
  templateUrl: './communication.component.html',
})
export class CommunicationComponent implements OnInit {
  communications?: ICommunication[];
  isLoading = false;

  constructor(protected communicationService: CommunicationService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.communicationService.query().subscribe({
      next: (res: HttpResponse<ICommunication[]>) => {
        this.isLoading = false;
        this.communications = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICommunication): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(communication: ICommunication): void {
    const modalRef = this.modalService.open(CommunicationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.communication = communication;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
