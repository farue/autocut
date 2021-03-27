import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { TransactionKind } from 'app/entities/enumerations/transaction-kind.model';
import { ITransaction, Transaction } from '../transaction.model';

import { TransactionService } from './transaction.service';

describe('Service Tests', () => {
  describe('Transaction Service', () => {
    let service: TransactionService;
    let httpMock: HttpTestingController;
    let elemDefault: ITransaction;
    let expectedResult: ITransaction | ITransaction[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TransactionService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        kind: TransactionKind.FEE,
        bookingDate: currentDate,
        valueDate: currentDate,
        value: 0,
        balanceAfter: 0,
        description: 'AAAAAAA',
        serviceQulifier: 'AAAAAAA',
        issuer: 'AAAAAAA',
        recipient: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            valueDate: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Transaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            valueDate: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.create(new Transaction()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Transaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            kind: 'BBBBBB',
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            valueDate: currentDate.format(DATE_TIME_FORMAT),
            value: 1,
            balanceAfter: 1,
            description: 'BBBBBB',
            serviceQulifier: 'BBBBBB',
            issuer: 'BBBBBB',
            recipient: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Transaction', () => {
        const patchObject = Object.assign(
          {
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            issuer: 'BBBBBB',
          },
          new Transaction()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Transaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            kind: 'BBBBBB',
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            valueDate: currentDate.format(DATE_TIME_FORMAT),
            value: 1,
            balanceAfter: 1,
            description: 'BBBBBB',
            serviceQulifier: 'BBBBBB',
            issuer: 'BBBBBB',
            recipient: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            bookingDate: currentDate,
            valueDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Transaction', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTransactionToCollectionIfMissing', () => {
        it('should add a Transaction to an empty array', () => {
          const transaction: ITransaction = { id: 123 };
          expectedResult = service.addTransactionToCollectionIfMissing([], transaction);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(transaction);
        });

        it('should not add a Transaction to an array that contains it', () => {
          const transaction: ITransaction = { id: 123 };
          const transactionCollection: ITransaction[] = [
            {
              ...transaction,
            },
            { id: 456 },
          ];
          expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, transaction);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Transaction to an array that doesn't contain it", () => {
          const transaction: ITransaction = { id: 123 };
          const transactionCollection: ITransaction[] = [{ id: 456 }];
          expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, transaction);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(transaction);
        });

        it('should add only unique Transaction to an array', () => {
          const transactionArray: ITransaction[] = [{ id: 123 }, { id: 456 }, { id: 16431 }];
          const transactionCollection: ITransaction[] = [{ id: 123 }];
          expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, ...transactionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const transaction: ITransaction = { id: 123 };
          const transaction2: ITransaction = { id: 456 };
          expectedResult = service.addTransactionToCollectionIfMissing([], transaction, transaction2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(transaction);
          expect(expectedResult).toContain(transaction2);
        });

        it('should accept null and undefined values', () => {
          const transaction: ITransaction = { id: 123 };
          expectedResult = service.addTransactionToCollectionIfMissing([], null, transaction, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(transaction);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
