jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BankTransactionService } from '../service/bank-transaction.service';
import { IBankTransaction, BankTransaction } from '../bank-transaction.model';
import { IBankAccount } from 'app/entities/bank-account/bank-account.model';
import { BankAccountService } from 'app/entities/bank-account/service/bank-account.service';
import { ITransactionBook } from 'app/entities/transaction-book/transaction-book.model';
import { TransactionBookService } from 'app/entities/transaction-book/service/transaction-book.service';

import { BankTransactionUpdateComponent } from './bank-transaction-update.component';

describe('Component Tests', () => {
  describe('BankTransaction Management Update Component', () => {
    let comp: BankTransactionUpdateComponent;
    let fixture: ComponentFixture<BankTransactionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let bankTransactionService: BankTransactionService;
    let bankAccountService: BankAccountService;
    let transactionBookService: TransactionBookService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BankTransactionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BankTransactionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BankTransactionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      bankTransactionService = TestBed.inject(BankTransactionService);
      bankAccountService = TestBed.inject(BankAccountService);
      transactionBookService = TestBed.inject(TransactionBookService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call BankAccount query and add missing value', () => {
        const bankTransaction: IBankTransaction = { id: 456 };
        const bankAccount: IBankAccount = { id: 5368 };
        bankTransaction.bankAccount = bankAccount;
        const contraBankAccount: IBankAccount = { id: 83157 };
        bankTransaction.contraBankAccount = contraBankAccount;

        const bankAccountCollection: IBankAccount[] = [{ id: 46289 }];
        spyOn(bankAccountService, 'query').and.returnValue(of(new HttpResponse({ body: bankAccountCollection })));
        const additionalBankAccounts = [bankAccount, contraBankAccount];
        const expectedCollection: IBankAccount[] = [...additionalBankAccounts, ...bankAccountCollection];
        spyOn(bankAccountService, 'addBankAccountToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        expect(bankAccountService.query).toHaveBeenCalled();
        expect(bankAccountService.addBankAccountToCollectionIfMissing).toHaveBeenCalledWith(
          bankAccountCollection,
          ...additionalBankAccounts
        );
        expect(comp.bankAccountsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call BankTransaction query and add missing value', () => {
        const bankTransaction: IBankTransaction = { id: 456 };
        const lefts: IBankTransaction[] = [{ id: 37781 }];
        bankTransaction.lefts = lefts;

        const bankTransactionCollection: IBankTransaction[] = [{ id: 45268 }];
        spyOn(bankTransactionService, 'query').and.returnValue(of(new HttpResponse({ body: bankTransactionCollection })));
        const additionalBankTransactions = [...lefts];
        const expectedCollection: IBankTransaction[] = [...additionalBankTransactions, ...bankTransactionCollection];
        spyOn(bankTransactionService, 'addBankTransactionToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        expect(bankTransactionService.query).toHaveBeenCalled();
        expect(bankTransactionService.addBankTransactionToCollectionIfMissing).toHaveBeenCalledWith(
          bankTransactionCollection,
          ...additionalBankTransactions
        );
        expect(comp.bankTransactionsSharedCollection).toEqual(expectedCollection);
      });

      it('Should call TransactionBook query and add missing value', () => {
        const bankTransaction: IBankTransaction = { id: 456 };
        const transactionBook: ITransactionBook = { id: 92007 };
        bankTransaction.transactionBook = transactionBook;

        const transactionBookCollection: ITransactionBook[] = [{ id: 7940 }];
        spyOn(transactionBookService, 'query').and.returnValue(of(new HttpResponse({ body: transactionBookCollection })));
        const additionalTransactionBooks = [transactionBook];
        const expectedCollection: ITransactionBook[] = [...additionalTransactionBooks, ...transactionBookCollection];
        spyOn(transactionBookService, 'addTransactionBookToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        expect(transactionBookService.query).toHaveBeenCalled();
        expect(transactionBookService.addTransactionBookToCollectionIfMissing).toHaveBeenCalledWith(
          transactionBookCollection,
          ...additionalTransactionBooks
        );
        expect(comp.transactionBooksSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const bankTransaction: IBankTransaction = { id: 456 };
        const bankAccount: IBankAccount = { id: 93857 };
        bankTransaction.bankAccount = bankAccount;
        const contraBankAccount: IBankAccount = { id: 22220 };
        bankTransaction.contraBankAccount = contraBankAccount;
        const lefts: IBankTransaction = { id: 10283 };
        bankTransaction.lefts = [lefts];
        const transactionBook: ITransactionBook = { id: 40719 };
        bankTransaction.transactionBook = transactionBook;

        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(bankTransaction));
        expect(comp.bankAccountsSharedCollection).toContain(bankAccount);
        expect(comp.bankAccountsSharedCollection).toContain(contraBankAccount);
        expect(comp.bankTransactionsSharedCollection).toContain(lefts);
        expect(comp.transactionBooksSharedCollection).toContain(transactionBook);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const bankTransaction = { id: 123 };
        spyOn(bankTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: bankTransaction }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(bankTransactionService.update).toHaveBeenCalledWith(bankTransaction);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const bankTransaction = new BankTransaction();
        spyOn(bankTransactionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: bankTransaction }));
        saveSubject.complete();

        // THEN
        expect(bankTransactionService.create).toHaveBeenCalledWith(bankTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const bankTransaction = { id: 123 };
        spyOn(bankTransactionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ bankTransaction });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(bankTransactionService.update).toHaveBeenCalledWith(bankTransaction);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackBankAccountById', () => {
        it('Should return tracked BankAccount primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBankAccountById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackBankTransactionById', () => {
        it('Should return tracked BankTransaction primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBankTransactionById(0, entity);
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
      describe('getSelectedBankTransaction', () => {
        it('Should return option if no BankTransaction is selected', () => {
          const option = { id: 123 };
          const result = comp.getSelectedBankTransaction(option);
          expect(result === option).toEqual(true);
        });

        it('Should return selected BankTransaction for according option', () => {
          const option = { id: 123 };
          const selected = { id: 123 };
          const selected2 = { id: 456 };
          const result = comp.getSelectedBankTransaction(option, [selected2, selected]);
          expect(result === selected).toEqual(true);
          expect(result === selected2).toEqual(false);
          expect(result === option).toEqual(false);
        });

        it('Should return option if this BankTransaction is not selected', () => {
          const option = { id: 123 };
          const selected = { id: 456 };
          const result = comp.getSelectedBankTransaction(option, [selected]);
          expect(result === option).toEqual(true);
          expect(result === selected).toEqual(false);
        });
      });
    });
  });
});
