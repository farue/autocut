import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IApartment, Apartment } from '../apartment.model';
import { ApartmentService } from '../service/apartment.service';
import { IInternetAccess } from 'app/entities/internet-access/internet-access.model';
import { InternetAccessService } from 'app/entities/internet-access/service/internet-access.service';
import { IAddress } from 'app/entities/address/address.model';
import { AddressService } from 'app/entities/address/service/address.service';

@Component({
  selector: 'jhi-apartment-update',
  templateUrl: './apartment-update.component.html',
})
export class ApartmentUpdateComponent implements OnInit {
  isSaving = false;

  internetAccessesCollection: IInternetAccess[] = [];
  addressesSharedCollection: IAddress[] = [];

  editForm = this.fb.group({
    id: [],
    nr: [null, [Validators.required]],
    type: [null, [Validators.required]],
    maxNumberOfLeases: [null, [Validators.required, Validators.min(0)]],
    internetAccess: [],
    address: [],
  });

  constructor(
    protected apartmentService: ApartmentService,
    protected internetAccessService: InternetAccessService,
    protected addressService: AddressService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apartment }) => {
      this.updateForm(apartment);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const apartment = this.createFromForm();
    if (apartment.id !== undefined) {
      this.subscribeToSaveResponse(this.apartmentService.update(apartment));
    } else {
      this.subscribeToSaveResponse(this.apartmentService.create(apartment));
    }
  }

  trackInternetAccessById(index: number, item: IInternetAccess): number {
    return item.id!;
  }

  trackAddressById(index: number, item: IAddress): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApartment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(apartment: IApartment): void {
    this.editForm.patchValue({
      id: apartment.id,
      nr: apartment.nr,
      type: apartment.type,
      maxNumberOfLeases: apartment.maxNumberOfLeases,
      internetAccess: apartment.internetAccess,
      address: apartment.address,
    });

    this.internetAccessesCollection = this.internetAccessService.addInternetAccessToCollectionIfMissing(
      this.internetAccessesCollection,
      apartment.internetAccess
    );
    this.addressesSharedCollection = this.addressService.addAddressToCollectionIfMissing(this.addressesSharedCollection, apartment.address);
  }

  protected loadRelationshipsOptions(): void {
    this.internetAccessService
      .query({ filter: 'apartment-is-null' })
      .pipe(map((res: HttpResponse<IInternetAccess[]>) => res.body ?? []))
      .pipe(
        map((internetAccesses: IInternetAccess[]) =>
          this.internetAccessService.addInternetAccessToCollectionIfMissing(internetAccesses, this.editForm.get('internetAccess')!.value)
        )
      )
      .subscribe((internetAccesses: IInternetAccess[]) => (this.internetAccessesCollection = internetAccesses));

    this.addressService
      .query()
      .pipe(map((res: HttpResponse<IAddress[]>) => res.body ?? []))
      .pipe(
        map((addresses: IAddress[]) => this.addressService.addAddressToCollectionIfMissing(addresses, this.editForm.get('address')!.value))
      )
      .subscribe((addresses: IAddress[]) => (this.addressesSharedCollection = addresses));
  }

  protected createFromForm(): IApartment {
    return {
      ...new Apartment(),
      id: this.editForm.get(['id'])!.value,
      nr: this.editForm.get(['nr'])!.value,
      type: this.editForm.get(['type'])!.value,
      maxNumberOfLeases: this.editForm.get(['maxNumberOfLeases'])!.value,
      internetAccess: this.editForm.get(['internetAccess'])!.value,
      address: this.editForm.get(['address'])!.value,
    };
  }
}
