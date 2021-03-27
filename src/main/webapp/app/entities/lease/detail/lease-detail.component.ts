import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILease } from '../lease.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-lease-detail',
  templateUrl: './lease-detail.component.html',
})
export class LeaseDetailComponent implements OnInit {
  lease: ILease | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lease }) => {
      this.lease = lease;
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
