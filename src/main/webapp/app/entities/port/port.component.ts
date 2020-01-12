import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPort } from 'app/shared/model/port.model';
import { PortService } from './port.service';
import { PortDeleteDialogComponent } from './port-delete-dialog.component';

@Component({
  selector: 'jhi-port',
  templateUrl: './port.component.html'
})
export class PortComponent implements OnInit, OnDestroy {
  ports?: IPort[];
  eventSubscriber?: Subscription;

  constructor(protected portService: PortService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.portService.query().subscribe((res: HttpResponse<IPort[]>) => {
      this.ports = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPorts();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPort): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPorts(): void {
    this.eventSubscriber = this.eventManager.subscribe('portListModification', () => this.loadAll());
  }

  delete(port: IPort): void {
    const modalRef = this.modalService.open(PortDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.port = port;
  }
}
