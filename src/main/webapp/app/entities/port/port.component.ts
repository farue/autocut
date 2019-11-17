import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IPort } from 'app/shared/model/port.model';
import { PortService } from './port.service';

@Component({
  selector: 'jhi-port',
  templateUrl: './port.component.html'
})
export class PortComponent implements OnInit, OnDestroy {
  ports: IPort[];
  eventSubscriber: Subscription;

  constructor(protected portService: PortService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.portService.query().subscribe((res: HttpResponse<IPort[]>) => {
      this.ports = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInPorts();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IPort) {
    return item.id;
  }

  registerChangeInPorts() {
    this.eventSubscriber = this.eventManager.subscribe('portListModification', () => this.loadAll());
  }
}
