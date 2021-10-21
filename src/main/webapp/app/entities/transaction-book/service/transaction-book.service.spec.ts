import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {TransactionBookType} from 'app/entities/enumerations/transaction-book-type.model';
import {ITransactionBook, TransactionBook} from '../transaction-book.model';

import {TransactionBookService} from './transaction-book.service';

describe('TransactionBook Service', () => {
  let service: TransactionBookService;
  let httpMock: HttpTestingController;
  let elemDefault: ITransactionBook;
  let expectedResult: ITransactionBook | ITransactionBook[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TransactionBookService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      type: TransactionBookType.CASH,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a TransactionBook', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TransactionBook()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TransactionBook', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TransactionBook', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          type: 'BBBBBB',
        },
        new TransactionBook()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TransactionBook', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a TransactionBook', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTransactionBookToCollectionIfMissing', () => {
      it('should add a TransactionBook to an empty array', () => {
        const transactionBook: ITransactionBook = { id: 123 };
        expectedResult = service.addTransactionBookToCollectionIfMissing([], transactionBook);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transactionBook);
      });

      it('should not add a TransactionBook to an array that contains it', () => {
        const transactionBook: ITransactionBook = { id: 123 };
        const transactionBookCollection: ITransactionBook[] = [
          {
            ...transactionBook,
          },
          { id: 456 },
        ];
        expectedResult = service.addTransactionBookToCollectionIfMissing(transactionBookCollection, transactionBook);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TransactionBook to an array that doesn't contain it", () => {
        const transactionBook: ITransactionBook = { id: 123 };
        const transactionBookCollection: ITransactionBook[] = [{ id: 456 }];
        expectedResult = service.addTransactionBookToCollectionIfMissing(transactionBookCollection, transactionBook);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transactionBook);
      });

      it('should add only unique TransactionBook to an array', () => {
        const transactionBookArray: ITransactionBook[] = [{ id: 123 }, { id: 456 }, { id: 21967 }];
        const transactionBookCollection: ITransactionBook[] = [{ id: 123 }];
        expectedResult = service.addTransactionBookToCollectionIfMissing(transactionBookCollection, ...transactionBookArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transactionBook: ITransactionBook = { id: 123 };
        const transactionBook2: ITransactionBook = { id: 456 };
        expectedResult = service.addTransactionBookToCollectionIfMissing([], transactionBook, transactionBook2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transactionBook);
        expect(expectedResult).toContain(transactionBook2);
      });

      it('should accept null and undefined values', () => {
        const transactionBook: ITransactionBook = { id: 123 };
        expectedResult = service.addTransactionBookToCollectionIfMissing([], null, transactionBook, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transactionBook);
      });

      it('should return initial array if no TransactionBook is added', () => {
        const transactionBookCollection: ITransactionBook[] = [{ id: 123 }];
        expectedResult = service.addTransactionBookToCollectionIfMissing(transactionBookCollection, undefined, null);
        expect(expectedResult).toEqual(transactionBookCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
