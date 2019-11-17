import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITenant } from 'app/shared/model/tenant.model';

@Component({
  selector: 'jhi-tenant-detail',
  templateUrl: './tenant-detail.component.html'
})
export class TenantDetailComponent implements OnInit {
  tenant: ITenant;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ tenant }) => {
      this.tenant = tenant;
    });
  }

  previousState() {
    window.history.back();
  }
}
