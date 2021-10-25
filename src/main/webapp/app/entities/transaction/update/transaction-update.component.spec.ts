jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TransactionService } from '../service/transaction.service';
import { ITransaction, Transaction } from '../transaction.model';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';

import { TransactionUpdateComponent } from './transaction-update.component';

describe('Transaction Management Update Component', () => {
  let comp: TransactionUpdateComponent;
  let fixture: ComponentFixture<TransactionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transactionService: TransactionService;
  let transactionBookService: TransactionBookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TransactionUpdateComponent],
      providers: [FormBuilder, ActivatedRoute],
    })
      .overrideTemplate(TransactionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transactionService = TestBed.inject(TransactionService);
    transactionBookService = TestBed.inject(TransactionBookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Transaction query and add missing value', () => {
      const transaction: ITransaction = { id: 456 };
      const lefts: ITransaction[] = [{ id: 51290 }];
      transaction.lefts = lefts;

      const transactionCollection: ITransaction[] = [{ id: 34604 }];
      jest.spyOn(transactionService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionCollection })));
      const additionalTransactions = [...lefts];
      const expectedCollection: ITransaction[] = [...additionalTransactions, ...transactionCollection];
      jest.spyOn(transactionService, 'addTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(transactionService.query).toHaveBeenCalled();
      expect(transactionService.addTransactionToCollectionIfMissing).toHaveBeenCalledWith(transactionCollection, ...additionalTransactions);
      expect(comp.transactionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TransactionBook query and add missing value', () => {
      const transaction: ITransaction = { id: 456 };
      const transactionBook: ITransactionBook = { id: 76059 };
      transaction.transactionBook = transactionBook;

      const transactionBookCollection: ITransactionBook[] = [{ id: 62086 }];
      jest.spyOn(transactionBookService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionBookCollection })));
      const additionalTransactionBooks = [transactionBook];
      const expectedCollection: ITransactionBook[] = [...additionalTransactionBooks, ...transactionBookCollection];
      jest.spyOn(transactionBookService, 'addTransactionBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(transactionBookService.query).toHaveBeenCalled();
      expect(transactionBookService.addTransactionBookToCollectionIfMissing).toHaveBeenCalledWith(
        transactionBookCollection,
        ...additionalTransactionBooks
      );
      expect(comp.transactionBooksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const transaction: ITransaction = { id: 456 };
      const lefts: ITransaction = { id: 17191 };
      transaction.lefts = [lefts];
      const transactionBook: ITransactionBook = { id: 3398 };
      transaction.transactionBook = transactionBook;

      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(transaction));
      expect(comp.transactionsSharedCollection).toContain(lefts);
      expect(comp.transactionBooksSharedCollection).toContain(transactionBook);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Transaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(transactionService.update).toHaveBeenCalledWith(transaction);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Transaction>>();
      const transaction = new Transaction();
      jest.spyOn(transactionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transaction }));
      saveSubject.complete();

      // THEN
      expect(transactionService.create).toHaveBeenCalledWith(transaction);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Transaction>>();
      const transaction = { id: 123 };
      jest.spyOn(transactionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transaction });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transactionService.update).toHaveBeenCalledWith(transaction);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTransactionById', () => {
      it('Should return tracked Transaction primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTransactionById(0, entity);
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
    describe('getSelectedTransaction', () => {
      it('Should return option if no Transaction is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedTransaction(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Transaction for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedTransaction(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Transaction is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedTransaction(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
