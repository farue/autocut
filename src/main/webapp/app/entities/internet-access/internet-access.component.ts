import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';

@Component({
  selector: 'jhi-internet-access',
  templateUrl: './internet-access.component.html'
})
export class InternetAccessComponent implements OnInit, OnDestroy {
  internetAccesses: IInternetAccess[];
  eventSubscriber: Subscription;

  constructor(protected internetAccessService: InternetAccessService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.internetAccessService.query().subscribe((res: HttpResponse<IInternetAccess[]>) => {
      this.internetAccesses = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInInternetAccesses();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IInternetAccess) {
    return item.id;
  }

  registerChangeInInternetAccesses() {
    this.eventSubscriber = this.eventManager.subscribe('internetAccessListModification', () => this.loadAll());
  }
}
