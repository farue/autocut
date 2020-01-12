import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from './apartment.service';
import { ApartmentDeleteDialogComponent } from './apartment-delete-dialog.component';

@Component({
  selector: 'jhi-apartment',
  templateUrl: './apartment.component.html'
})
export class ApartmentComponent implements OnInit, OnDestroy {
  apartments?: IApartment[];
  eventSubscriber?: Subscription;

  constructor(protected apartmentService: ApartmentService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.apartmentService.query().subscribe((res: HttpResponse<IApartment[]>) => {
      this.apartments = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInApartments();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IApartment): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInApartments(): void {
    this.eventSubscriber = this.eventManager.subscribe('apartmentListModification', () => this.loadAll());
  }

  delete(apartment: IApartment): void {
    const modalRef = this.modalService.open(ApartmentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.apartment = apartment;
  }
}
