jest.mock('@angular/router');

import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {FormBuilder} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {of, Subject} from 'rxjs';

import {ApartmentService} from '../service/apartment.service';
import {Apartment, IApartment} from '../apartment.model';
import {IInternetAccess} from 'app/entities/internet-access/internet-access.model';
import {InternetAccessService} from 'app/entities/internet-access/service/internet-access.service';
import {IAddress} from 'app/entities/address/address.model';
import {AddressService} from 'app/entities/address/service/address.service';

import {ApartmentUpdateComponent} from './apartment-update.component';

describe('Component Tests', () => {
  describe('Apartment Management Update Component', () => {
    let comp: ApartmentUpdateComponent;
    let fixture: ComponentFixture<ApartmentUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let apartmentService: ApartmentService;
    let internetAccessService: InternetAccessService;
    let addressService: AddressService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [ApartmentUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(ApartmentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ApartmentUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      apartmentService = TestBed.inject(ApartmentService);
      internetAccessService = TestBed.inject(InternetAccessService);
      addressService = TestBed.inject(AddressService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call internetAccess query and add missing value', () => {
        const apartment: IApartment = { id: 456 };
        const internetAccess: IInternetAccess = { id: 25909 };
        apartment.internetAccess = internetAccess;

        const internetAccessCollection: IInternetAccess[] = [{ id: 51624 }];
        jest.spyOn(internetAccessService, 'query').mockReturnValue(of(new HttpResponse({ body: internetAccessCollection })));
        const expectedCollection: IInternetAccess[] = [internetAccess, ...internetAccessCollection];
        jest.spyOn(internetAccessService, 'addInternetAccessToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ apartment });
        comp.ngOnInit();

        expect(internetAccessService.query).toHaveBeenCalled();
        expect(internetAccessService.addInternetAccessToCollectionIfMissing).toHaveBeenCalledWith(internetAccessCollection, internetAccess);
        expect(comp.internetAccessesCollection).toEqual(expectedCollection);
      });

      it('Should call Address query and add missing value', () => {
        const apartment: IApartment = { id: 456 };
        const address: IAddress = { id: 53880 };
        apartment.address = address;

        const addressCollection: IAddress[] = [{ id: 84572 }];
        jest.spyOn(addressService, 'query').mockReturnValue(of(new HttpResponse({ body: addressCollection })));
        const additionalAddresses = [address];
        const expectedCollection: IAddress[] = [...additionalAddresses, ...addressCollection];
        jest.spyOn(addressService, 'addAddressToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ apartment });
        comp.ngOnInit();

        expect(addressService.query).toHaveBeenCalled();
        expect(addressService.addAddressToCollectionIfMissing).toHaveBeenCalledWith(addressCollection, ...additionalAddresses);
        expect(comp.addressesSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const apartment: IApartment = { id: 456 };
        const internetAccess: IInternetAccess = { id: 48042 };
        apartment.internetAccess = internetAccess;
        const address: IAddress = { id: 64773 };
        apartment.address = address;

        activatedRoute.data = of({ apartment });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(apartment));
        expect(comp.internetAccessesCollection).toContain(internetAccess);
        expect(comp.addressesSharedCollection).toContain(address);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Apartment>>();
        const apartment = { id: 123 };
        jest.spyOn(apartmentService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ apartment });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: apartment }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(apartmentService.update).toHaveBeenCalledWith(apartment);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Apartment>>();
        const apartment = new Apartment();
        jest.spyOn(apartmentService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ apartment });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: apartment }));
        saveSubject.complete();

        // THEN
        expect(apartmentService.create).toHaveBeenCalledWith(apartment);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Apartment>>();
        const apartment = { id: 123 };
        jest.spyOn(apartmentService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ apartment });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(apartmentService.update).toHaveBeenCalledWith(apartment);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackInternetAccessById', () => {
        it('Should return tracked InternetAccess primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackInternetAccessById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackAddressById', () => {
        it('Should return tracked Address primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackAddressById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
