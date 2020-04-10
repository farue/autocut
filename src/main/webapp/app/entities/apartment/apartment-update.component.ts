import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { IApartment, Apartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from './apartment.service';
import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';
import { IAddress } from 'app/shared/model/address.model';
import { AddressService } from 'app/entities/address/address.service';

type SelectableEntity = IInternetAccess | IAddress;

@Component({
  selector: 'jhi-apartment-update',
  templateUrl: './apartment-update.component.html'
})
export class ApartmentUpdateComponent implements OnInit {
  isSaving = false;
  internetaccesses: IInternetAccess[] = [];
  addresses: IAddress[] = [];

  editForm = this.fb.group({
    id: [],
    apartmentNr: [null, [Validators.required]],
    apartmentType: [null, [Validators.required]],
    maxNumberOfLeases: [null, [Validators.required, Validators.min(0)]],
    internetAccess: [],
    address: []
  });

  constructor(
    protected apartmentService: ApartmentService,
    protected internetAccessService: InternetAccessService,
    protected addressService: AddressService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ apartment }) => {
      this.updateForm(apartment);

      this.internetAccessService
        .query({ filter: 'apartment-is-null' })
        .pipe(
          map((res: HttpResponse<IInternetAccess[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IInternetAccess[]) => {
          if (!apartment.internetAccess || !apartment.internetAccess.id) {
            this.internetaccesses = resBody;
          } else {
            this.internetAccessService
              .find(apartment.internetAccess.id)
              .pipe(
                map((subRes: HttpResponse<IInternetAccess>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IInternetAccess[]) => (this.internetaccesses = concatRes));
          }
        });

      this.addressService.query().subscribe((res: HttpResponse<IAddress[]>) => (this.addresses = res.body || []));
    });
  }

  updateForm(apartment: IApartment): void {
    this.editForm.patchValue({
      id: apartment.id,
      apartmentNr: apartment.apartmentNr,
      apartmentType: apartment.apartmentType,
      maxNumberOfLeases: apartment.maxNumberOfLeases,
      internetAccess: apartment.internetAccess,
      address: apartment.address
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

  private createFromForm(): IApartment {
    return {
      ...new Apartment(),
      id: this.editForm.get(['id'])!.value,
      apartmentNr: this.editForm.get(['apartmentNr'])!.value,
      apartmentType: this.editForm.get(['apartmentType'])!.value,
      maxNumberOfLeases: this.editForm.get(['maxNumberOfLeases'])!.value,
      internetAccess: this.editForm.get(['internetAccess'])!.value,
      address: this.editForm.get(['address'])!.value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApartment>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }
}
