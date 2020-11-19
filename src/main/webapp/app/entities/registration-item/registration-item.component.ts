import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRegistrationItem } from 'app/shared/model/registration-item.model';
import { RegistrationItemService } from './registration-item.service';
import { RegistrationItemDeleteDialogComponent } from './registration-item-delete-dialog.component';

@Component({
  selector: 'jhi-registration-item',
  templateUrl: './registration-item.component.html',
})
export class RegistrationItemComponent implements OnInit, OnDestroy {
  registrationItems?: IRegistrationItem[];
  eventSubscriber?: Subscription;

  constructor(
    protected registrationItemService: RegistrationItemService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.registrationItemService.query().subscribe((res: HttpResponse<IRegistrationItem[]>) => (this.registrationItems = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInRegistrationItems();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IRegistrationItem): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInRegistrationItems(): void {
    this.eventSubscriber = this.eventManager.subscribe('registrationItemListModification', () => this.loadAll());
  }

  delete(registrationItem: IRegistrationItem): void {
    const modalRef = this.modalService.open(RegistrationItemDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.registrationItem = registrationItem;
  }
}
