import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IInternalTransaction, InternalTransaction } from '../internal-transaction.model';

import { InternalTransactionService } from './internal-transaction.service';

describe('InternalTransaction Service', () => {
  let service: InternalTransactionService;
  let httpMock: HttpTestingController;
  let elemDefault: IInternalTransaction;
  let expectedResult: IInternalTransaction | IInternalTransaction[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(InternalTransactionService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      type: 'AAAAAAA',
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

    it('should create a InternalTransaction', () => {
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

      service.create(new InternalTransaction()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a InternalTransaction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
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

    it('should partial update a InternalTransaction', () => {
      const patchObject = Object.assign(
        {
          type: 'BBBBBB',
          value: 1,
          balanceAfter: 1,
          description: 'BBBBBB',
          issuer: 'BBBBBB',
          recipient: 'BBBBBB',
        },
        new InternalTransaction()
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

    it('should return a list of InternalTransaction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
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

    it('should delete a InternalTransaction', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addInternalTransactionToCollectionIfMissing', () => {
      it('should add a InternalTransaction to an empty array', () => {
        const internalTransaction: IInternalTransaction = { id: 123 };
        expectedResult = service.addInternalTransactionToCollectionIfMissing([], internalTransaction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(internalTransaction);
      });

      it('should not add a InternalTransaction to an array that contains it', () => {
        const internalTransaction: IInternalTransaction = { id: 123 };
        const internalTransactionCollection: IInternalTransaction[] = [
          {
            ...internalTransaction,
          },
          { id: 456 },
        ];
        expectedResult = service.addInternalTransactionToCollectionIfMissing(internalTransactionCollection, internalTransaction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a InternalTransaction to an array that doesn't contain it", () => {
        const internalTransaction: IInternalTransaction = { id: 123 };
        const internalTransactionCollection: IInternalTransaction[] = [{ id: 456 }];
        expectedResult = service.addInternalTransactionToCollectionIfMissing(internalTransactionCollection, internalTransaction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(internalTransaction);
      });

      it('should add only unique InternalTransaction to an array', () => {
        const internalTransactionArray: IInternalTransaction[] = [{ id: 123 }, { id: 456 }, { id: 74729 }];
        const internalTransactionCollection: IInternalTransaction[] = [{ id: 123 }];
        expectedResult = service.addInternalTransactionToCollectionIfMissing(internalTransactionCollection, ...internalTransactionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const internalTransaction: IInternalTransaction = { id: 123 };
        const internalTransaction2: IInternalTransaction = { id: 456 };
        expectedResult = service.addInternalTransactionToCollectionIfMissing([], internalTransaction, internalTransaction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(internalTransaction);
        expect(expectedResult).toContain(internalTransaction2);
      });

      it('should accept null and undefined values', () => {
        const internalTransaction: IInternalTransaction = { id: 123 };
        expectedResult = service.addInternalTransactionToCollectionIfMissing([], null, internalTransaction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(internalTransaction);
      });

      it('should return initial array if no InternalTransaction is added', () => {
        const internalTransactionCollection: IInternalTransaction[] = [{ id: 123 }];
        expectedResult = service.addInternalTransactionToCollectionIfMissing(internalTransactionCollection, undefined, null);
        expect(expectedResult).toEqual(internalTransactionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
