import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { IAddress } from 'app/shared/model/address.model';
import { AddressService } from './address.service';

@Component({
  selector: 'jhi-address',
  templateUrl: './address.component.html'
})
export class AddressComponent implements OnInit, OnDestroy {
  addresses: IAddress[];
  eventSubscriber: Subscription;

  constructor(protected addressService: AddressService, protected eventManager: JhiEventManager) {}

  loadAll() {
    this.addressService.query().subscribe((res: HttpResponse<IAddress[]>) => {
      this.addresses = res.body;
    });
  }

  ngOnInit() {
    this.loadAll();
    this.registerChangeInAddresses();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IAddress) {
    return item.id;
  }

  registerChangeInAddresses() {
    this.eventSubscriber = this.eventManager.subscribe('addressListModification', () => this.loadAll());
  }
}
