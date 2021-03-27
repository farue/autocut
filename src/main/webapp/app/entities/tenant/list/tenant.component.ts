import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITenant } from '../tenant.model';
import { TenantService } from '../service/tenant.service';
import { TenantDeleteDialogComponent } from '../delete/tenant-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-tenant',
  templateUrl: './tenant.component.html',
})
export class TenantComponent implements OnInit {
  tenants?: ITenant[];
  isLoading = false;

  constructor(protected tenantService: TenantService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.tenantService.query().subscribe(
      (res: HttpResponse<ITenant[]>) => {
        this.isLoading = false;
        this.tenants = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITenant): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(tenant: ITenant): void {
    const modalRef = this.modalService.open(TenantDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tenant = tenant;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
