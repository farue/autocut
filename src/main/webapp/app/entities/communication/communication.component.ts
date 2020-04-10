import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICommunication } from 'app/shared/model/communication.model';
import { CommunicationService } from './communication.service';
import { CommunicationDeleteDialogComponent } from './communication-delete-dialog.component';

@Component({
  selector: 'jhi-communication',
  templateUrl: './communication.component.html'
})
export class CommunicationComponent implements OnInit, OnDestroy {
  communications?: ICommunication[];
  eventSubscriber?: Subscription;

  constructor(
    protected communicationService: CommunicationService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.communicationService.query().subscribe((res: HttpResponse<ICommunication[]>) => (this.communications = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCommunications();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICommunication): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType: string, base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInCommunications(): void {
    this.eventSubscriber = this.eventManager.subscribe('communicationListModification', () => this.loadAll());
  }

  delete(communication: ICommunication): void {
    const modalRef = this.modalService.open(CommunicationDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.communication = communication;
  }
}
