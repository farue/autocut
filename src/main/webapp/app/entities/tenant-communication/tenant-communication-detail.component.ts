import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ITenantCommunication } from 'app/shared/model/tenant-communication.model';

@Component({
  selector: 'jhi-tenant-communication-detail',
  templateUrl: './tenant-communication-detail.component.html'
})
export class TenantCommunicationDetailComponent implements OnInit {
  tenantCommunication: ITenantCommunication;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ tenantCommunication }) => {
      this.tenantCommunication = tenantCommunication;
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }
  previousState() {
    window.history.back();
  }
}
