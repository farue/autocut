import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';
import { Apartment, IApartment } from 'app/shared/model/apartment.model';
import { ApartmentService } from './apartment.service';
import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';
import { IAddress } from 'app/shared/model/address.model';
import { AddressService } from 'app/entities/address/address.service';

@Component({
  selector: 'jhi-apartment-update',
  templateUrl: './apartment-update.component.html'
})
export class ApartmentUpdateComponent implements OnInit {
  isSaving: boolean;

  internetaccesses: IInternetAccess[];

  addresses: IAddress[];

  editForm = this.fb.group({
    id: [],
    apartmentNr: [null, [Validators.required]],
    apartmentType: [null, [Validators.required]],
    internetAccess: [null, Validators.required],
    address: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected apartmentService: ApartmentService,
    protected internetAccessService: InternetAccessService,
    protected addressService: AddressService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ apartment }) => {
      this.updateForm(apartment);
    });
    this.internetAccessService.query({ filter: 'apartment-is-null' }).subscribe(
      (res: HttpResponse<IInternetAccess[]>) => {
        if (!this.editForm.get('internetAccess').value || !this.editForm.get('internetAccess').value.id) {
          this.internetaccesses = res.body;
        } else {
          this.internetAccessService
            .find(this.editForm.get('internetAccess').value.id)
            .subscribe(
              (subRes: HttpResponse<IInternetAccess>) => (this.internetaccesses = [subRes.body].concat(res.body)),
              (subRes: HttpErrorResponse) => this.onError(subRes.message)
            );
        }
      },
      (res: HttpErrorResponse) => this.onError(res.message)
    );
    this.addressService
      .query()
      .subscribe((res: HttpResponse<IAddress[]>) => (this.addresses = res.body), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(apartment: IApartment) {
    this.editForm.patchValue({
      id: apartment.id,
      apartmentNr: apartment.apartmentNr,
      apartmentType: apartment.apartmentType,
      internetAccess: apartment.internetAccess,
      address: apartment.address
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
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
      id: this.editForm.get(['id']).value,
      apartmentNr: this.editForm.get(['apartmentNr']).value,
      apartmentType: this.editForm.get(['apartmentType']).value,
      internetAccess: this.editForm.get(['internetAccess']).value,
      address: this.editForm.get(['address']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IApartment>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackInternetAccessById(index: number, item: IInternetAccess) {
    return item.id;
  }

  trackAddressById(index: number, item: IAddress) {
    return item.id;
  }
}
