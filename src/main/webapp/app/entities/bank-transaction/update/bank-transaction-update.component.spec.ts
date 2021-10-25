jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BankTransactionService } from '../service/bank-transaction.service';
import { BankTransaction, IBankTransaction } from '../bank-transaction.model';
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
        const bankAccount: IBankAccount = { id: 74117 };
        bankTransaction.bankAccount = bankAccount;
        const contraBankAccount: IBankAccount = { id: 86507 };
        bankTransaction.contraBankAccount = contraBankAccount;

        const bankAccountCollection: IBankAccount[] = [{ id: 3091 }];
        jest.spyOn(bankAccountService, 'query').mockReturnValue(of(new HttpResponse({ body: bankAccountCollection })));
        const additionalBankAccounts = [bankAccount, contraBankAccount];
        const expectedCollection: IBankAccount[] = [...additionalBankAccounts, ...bankAccountCollection];
        jest.spyOn(bankAccountService, 'addBankAccountToCollectionIfMissing').mockReturnValue(expectedCollection);

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
        const lefts: IBankTransaction[] = [{ id: 78315 }];
        bankTransaction.lefts = lefts;

        const bankTransactionCollection: IBankTransaction[] = [{ id: 79681 }];
        jest.spyOn(bankTransactionService, 'query').mockReturnValue(of(new HttpResponse({ body: bankTransactionCollection })));
        const additionalBankTransactions = [...lefts];
        const expectedCollection: IBankTransaction[] = [...additionalBankTransactions, ...bankTransactionCollection];
        jest.spyOn(bankTransactionService, 'addBankTransactionToCollectionIfMissing').mockReturnValue(expectedCollection);

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
        const transactionBook: ITransactionBook = { id: 24443 };
        bankTransaction.transactionBook = transactionBook;

        const transactionBookCollection: ITransactionBook[] = [{ id: 34791 }];
        jest.spyOn(transactionBookService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionBookCollection })));
        const additionalTransactionBooks = [transactionBook];
        const expectedCollection: ITransactionBook[] = [...additionalTransactionBooks, ...transactionBookCollection];
        jest.spyOn(transactionBookService, 'addTransactionBookToCollectionIfMissing').mockReturnValue(expectedCollection);

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
        const bankAccount: IBankAccount = { id: 75383 };
        bankTransaction.bankAccount = bankAccount;
        const contraBankAccount: IBankAccount = { id: 79965 };
        bankTransaction.contraBankAccount = contraBankAccount;
        const lefts: IBankTransaction = { id: 68939 };
        bankTransaction.lefts = [lefts];
        const transactionBook: ITransactionBook = { id: 15120 };
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
        const saveSubject = new Subject<HttpResponse<BankTransaction>>();
        const bankTransaction = { id: 123 };
        jest.spyOn(bankTransactionService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
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
        const saveSubject = new Subject<HttpResponse<BankTransaction>>();
        const bankTransaction = new BankTransaction();
        jest.spyOn(bankTransactionService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
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
        const saveSubject = new Subject<HttpResponse<BankTransaction>>();
        const bankTransaction = { id: 123 };
        jest.spyOn(bankTransactionService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
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
