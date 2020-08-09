import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';

@Component({
  selector: 'jhi-tenant-communication-detail',
  templateUrl: './tenant-communication-detail.component.html',
})
export class TenantCommunicationDetailComponent implements OnInit {
  tenantCommunication: ITenantCommunication | null = null;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => (this.tenantCommunication = tenantCommunication));
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType = '', base64String: string): void {
    this.dataUtils.openFile(contentType, base64String);
  }

  previousState(): void {
    window.history.back();
  }
}
