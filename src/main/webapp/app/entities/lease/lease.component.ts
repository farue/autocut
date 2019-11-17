import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ILease } from 'app/shared/model/lease.model';
import { LeaseService } from './lease.service';

@Component({
  selector: 'jhi-lease',
  templateUrl: './lease.component.html'
})
export class LeaseComponent implements OnInit, OnDestroy {
  leases: ILease[];
  eventSubscriber: Subscription;

  constructor(protected leaseService: LeaseService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.leaseService.query().subscribe((res: HttpResponse<ILease[]>) => {
      this.leases = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInLeases();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: ILease) {
    return item.id;
  }

  registerChangeInLeases() {
    this.eventSubscriber = this.eventManager.subscribe('leaseListModification', () => this.loadAll());
  }
}
