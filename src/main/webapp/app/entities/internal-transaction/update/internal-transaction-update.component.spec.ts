jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { InternalTransactionService } from '../service/internal-transaction.service';
import { IInternalTransaction, InternalTransaction } from '../internal-transaction.model';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';

import { InternalTransactionUpdateComponent } from './internal-transaction-update.component';

describe('Component Tests', () => {
  describe('InternalTransaction Management Update Component', () => {
    let comp: InternalTransactionUpdateComponent;
    let fixture: ComponentFixture<InternalTransactionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let internalTransactionService: InternalTransactionService;
    let transactionBookService: TransactionBookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [InternalTransactionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(InternalTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternalTransactionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      internalTransactionService = TestBed.inject(InternalTransactionService);
      transactionBookService = TestBed.inject(TransactionBookService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call InternalTransaction query and add missing value', () => {
        const internalTransaction: IInternalTransaction = { id: 456 };
        const lefts: IInternalTransaction[] = [{ id: 75979 }];
        internalTransaction.lefts = lefts;

        const internalTransactionCollection: IInternalTransaction[] = [{ id: 24572 }];
        spyOn(internalTransactionService, 'query').and.returnValue(of(new HttpResponse({ body: internalTransactionCollection })));
        const additionalInternalTransactions = [...lefts];
        const expectedCollection: IInternalTransaction[] = [...additionalInternalTransactions, ...internalTransactionCollection];
        spyOn(internalTransactionService, 'addInternalTransactionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ internalTransaction });
        comp.ngOnInit();

        expect(internalTransactionService.query).toHaveBeenCalled();
        expect(internalTransactionService.addInternalTransactionToCollectionIfMissing).toHaveBeenCalledWith(
          internalTransactionCollection,
          ...additionalInternalTransactions
        );
        expect(comp.internalTransactionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TransactionBook query and add missing value', () => {
        const internalTransaction: IInternalTransaction = { id: 456 };
        const transactionBook: ITransactionBook = { id: 32942 };
        internalTransaction.transactionBook = transactionBook;

        const transactionBookCollection: ITransactionBook[] = [{ id: 94846 }];
        spyOn(transactionBookService, 'query').and.returnValue(of(new HttpResponse({ body: transactionBookCollection })));
        const additionalTransactionBooks = [transactionBook];
        const expectedCollection: ITransactionBook[] = [...additionalTransactionBooks, ...transactionBookCollection];
        spyOn(transactionBookService, 'addTransactionBookToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ internalTransaction });
        comp.ngOnInit();

        expect(transactionBookService.query).toHaveBeenCalled();
        expect(transactionBookService.addTransactionBookToCollectionIfMissing).toHaveBeenCalledWith(
          transactionBookCollection,
          ...additionalTransactionBooks
        );
        expect(comp.transactionBooksSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const internalTransaction: IInternalTransaction = { id: 456 };
        const lefts: IInternalTransaction = { id: 39807 };
        internalTransaction.lefts = [lefts];
        const transactionBook: ITransactionBook = { id: 15285 };
        internalTransaction.transactionBook = transactionBook;

        activatedRoute.data = of({ internalTransaction });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(internalTransaction));
        expect(comp.internalTransactionsSharedCollection).toContain(lefts);
        expect(comp.transactionBooksSharedCollection).toContain(transactionBook);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const internalTransaction = { id: 123 };
        spyOn(internalTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ internalTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: internalTransaction }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(internalTransactionService.update).toHaveBeenCalledWith(internalTransaction);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const internalTransaction = new InternalTransaction();
        spyOn(internalTransactionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ internalTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: internalTransaction }));
        saveSubject.complete();

        // THEN
        expect(internalTransactionService.create).toHaveBeenCalledWith(internalTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const internalTransaction = { id: 123 };
        spyOn(internalTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ internalTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(internalTransactionService.update).toHaveBeenCalledWith(internalTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackInternalTransactionById', () => {
        it('Should return tracked InternalTransaction primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackInternalTransactionById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTransactionBookById', () => {
        it('Should return tracked TransactionBook primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTransactionBookById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });

    describe('Getting selected relationships', () => {
      describe('getSelectedInternalTransaction', () => {
        it('Should return option if no InternalTransaction is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedInternalTransaction(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected InternalTransaction for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedInternalTransaction(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this InternalTransaction is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedInternalTransaction(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
