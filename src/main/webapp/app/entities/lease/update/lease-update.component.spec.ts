jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LeaseService } from '../service/lease.service';
import { ILease, Lease } from '../lease.model';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';
import { IApartment } from 'app/entities/apartment/apartment.model';
import { ApartmentService } from 'app/entities/apartment/service/apartment.service';

import { LeaseUpdateComponent } from './lease-update.component';

describe('Lease Management Update Component', () => {
  let comp: LeaseUpdateComponent;
  let fixture: ComponentFixture<LeaseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let leaseService: LeaseService;
  let transactionBookService: TransactionBookService;
  let apartmentService: ApartmentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LeaseUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(LeaseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LeaseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    leaseService = TestBed.inject(LeaseService);
    transactionBookService = TestBed.inject(TransactionBookService);
    apartmentService = TestBed.inject(ApartmentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TransactionBook query and add missing value', () => {
      const lease: ILease = { id: 456 };
      const transactionBooks: ITransactionBook[] = [{ id: 14392 }];
      lease.transactionBooks = transactionBooks;

      const transactionBookCollection: ITransactionBook[] = [{ id: 73850 }];
      jest.spyOn(transactionBookService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionBookCollection })));
      const additionalTransactionBooks = [...transactionBooks];
      const expectedCollection: ITransactionBook[] = [...additionalTransactionBooks, ...transactionBookCollection];
      jest.spyOn(transactionBookService, 'addTransactionBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lease });
      comp.ngOnInit();

      expect(transactionBookService.query).toHaveBeenCalled();
      expect(transactionBookService.addTransactionBookToCollectionIfMissing).toHaveBeenCalledWith(
        transactionBookCollection,
        ...additionalTransactionBooks
      );
      expect(comp.transactionBooksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Apartment query and add missing value', () => {
      const lease: ILease = { id: 456 };
      const apartment: IApartment = { id: 21618 };
      lease.apartment = apartment;

      const apartmentCollection: IApartment[] = [{ id: 68920 }];
      jest.spyOn(apartmentService, 'query').mockReturnValue(of(new HttpResponse({ body: apartmentCollection })));
      const additionalApartments = [apartment];
      const expectedCollection: IApartment[] = [...additionalApartments, ...apartmentCollection];
      jest.spyOn(apartmentService, 'addApartmentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lease });
      comp.ngOnInit();

      expect(apartmentService.query).toHaveBeenCalled();
      expect(apartmentService.addApartmentToCollectionIfMissing).toHaveBeenCalledWith(apartmentCollection, ...additionalApartments);
      expect(comp.apartmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const lease: ILease = { id: 456 };
      const transactionBooks: ITransactionBook = { id: 63945 };
      lease.transactionBooks = [transactionBooks];
      const apartment: IApartment = { id: 11654 };
      lease.apartment = apartment;

      activatedRoute.data = of({ lease });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(lease));
      expect(comp.transactionBooksSharedCollection).toContain(transactionBooks);
      expect(comp.apartmentsSharedCollection).toContain(apartment);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lease>>();
      const lease = { id: 123 };
      jest.spyOn(leaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lease });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lease }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(leaseService.update).toHaveBeenCalledWith(lease);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lease>>();
      const lease = new Lease();
      jest.spyOn(leaseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lease });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lease }));
      saveSubject.complete();

      // THEN
      expect(leaseService.create).toHaveBeenCalledWith(lease);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lease>>();
      const lease = { id: 123 };
      jest.spyOn(leaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lease });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(leaseService.update).toHaveBeenCalledWith(lease);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTransactionBookById', () => {
      it('Should return tracked TransactionBook primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTransactionBookById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackApartmentById', () => {
      it('Should return tracked Apartment primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackApartmentById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedTransactionBook', () => {
      it('Should return option if no TransactionBook is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedTransactionBook(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected TransactionBook for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedTransactionBook(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this TransactionBook is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedTransactionBook(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
