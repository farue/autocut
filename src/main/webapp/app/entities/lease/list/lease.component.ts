import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILease } from '../lease.model';
import { LeaseService } from '../service/lease.service';
import { LeaseDeleteDialogComponent } from '../delete/lease-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-lease',
  templateUrl: './lease.component.html',
})
export class LeaseComponent implements OnInit {
  leases?: ILease[];
  isLoading = false;

  constructor(protected leaseService: LeaseService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.leaseService.query().subscribe(
      (res: HttpResponse<ILease[]>) => {
        this.isLoading = false;
        this.leases = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ILease): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(lease: ILease): void {
    const modalRef = this.modalService.open(LeaseDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.lease = lease;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
