import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITenantCommunication } from '../tenant-communication.model';
import { TenantCommunicationService } from '../service/tenant-communication.service';
import { TenantCommunicationDeleteDialogComponent } from '../delete/tenant-communication-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-tenant-communication',
  templateUrl: './tenant-communication.component.html',
})
export class TenantCommunicationComponent implements OnInit {
  tenantCommunications?: ITenantCommunication[];
  isLoading = false;

  constructor(
    protected tenantCommunicationService: TenantCommunicationService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.isLoading = true;

    this.tenantCommunicationService.query().subscribe(
      (res: HttpResponse<ITenantCommunication[]>) => {
        this.isLoading = false;
        this.tenantCommunications = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITenantCommunication): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(tenantCommunication: ITenantCommunication): void {
    const modalRef = this.modalService.open(TenantCommunicationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tenantCommunication = tenantCommunication;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
