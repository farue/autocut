import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILease } from 'app/shared/model/lease.model';

@Component({
  selector: 'jhi-lease-detail',
  templateUrl: './lease-detail.component.html'
})
export class LeaseDetailComponent implements OnInit {
  lease: ILease;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ lease }) => {
      this.lease = lease;
    });
  }

  previousState() {
    window.history.back();
  }
}
