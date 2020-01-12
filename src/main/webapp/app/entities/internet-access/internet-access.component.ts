import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from './internet-access.service';
import { InternetAccessDeleteDialogComponent } from './internet-access-delete-dialog.component';

@Component({
  selector: 'jhi-internet-access',
  templateUrl: './internet-access.component.html'
})
export class InternetAccessComponent implements OnInit, OnDestroy {
  internetAccesses?: IInternetAccess[];
  eventSubscriber?: Subscription;

  constructor(
    protected internetAccessService: InternetAccessService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.internetAccessService.query().subscribe((res: HttpResponse<IInternetAccess[]>) => {
      this.internetAccesses = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInInternetAccesses();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IInternetAccess): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInInternetAccesses(): void {
    this.eventSubscriber = this.eventManager.subscribe('internetAccessListModification', () => this.loadAll());
  }

  delete(internetAccess: IInternetAccess): void {
    const modalRef = this.modalService.open(InternetAccessDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.internetAccess = internetAccess;
  }
}
