import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITenantCommunication } from '../tenant-communication.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-tenant-communication-detail',
  templateUrl: './tenant-communication-detail.component.html',
})
export class TenantCommunicationDetailComponent implements OnInit {
  tenantCommunication: ITenantCommunication | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => {
      this.tenantCommunication = tenantCommunication;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
