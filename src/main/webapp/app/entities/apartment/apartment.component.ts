import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from './apartment.service';

@Component({
  selector: 'jhi-apartment',
  templateUrl: './apartment.component.html'
})
export class ApartmentComponent implements OnInit, OnDestroy {
  apartments: IApartment[];
  eventSubscriber: Subscription;

  constructor(protected apartmentService: ApartmentService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.apartmentService.query().subscribe((res: HttpResponse<IApartment[]>) => {
      this.apartments = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInApartments();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IApartment) {
    return item.id;
  }

  registerChangeInApartments() {
    this.eventSubscriber = this.eventManager.subscribe('apartmentListModification', () => this.loadAll());
  }
}
