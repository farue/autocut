import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { BankTransactionService } from 'app/entities/bank-transaction/bank-transaction.service';
import { BankTransaction, IBankTransaction } from 'app/shared/model/bank-transaction.model';

describe('Service Tests', () => {
  describe('BankTransaction Service', () => {
    let injector: TestBed;
    let service: BankTransactionService;
    let httpMock: HttpTestingController;
    let elemDefault: IBankTransaction;
    let expectedResult: IBankTransaction | IBankTransaction[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(BankTransactionService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new BankTransaction(
        0,
        currentDate,
        currentDate,
        0,
        0,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        undefined,
        undefined,
        undefined,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA'
      );
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

      it('should create a BankTransaction', () => {
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

        service.create(new BankTransaction()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a BankTransaction', () => {
        const returnedFromService = Object.assign(
          {
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            valueDate: currentDate.format(DATE_TIME_FORMAT),
            value: 1,
            balanceAfter: 1,
            type: 'BBBBBB',
            description: 'BBBBBB',
            customerRef: 'BBBBBB',
            gvCode: 'BBBBBB',
            endToEnd: 'BBBBBB',
            primanota: 'BBBBBB',
            creditor: 'BBBBBB',
            mandate: 'BBBBBB',
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

      it('should return a list of BankTransaction', () => {
        const returnedFromService = Object.assign(
          {
            bookingDate: currentDate.format(DATE_TIME_FORMAT),
            valueDate: currentDate.format(DATE_TIME_FORMAT),
            value: 1,
            balanceAfter: 1,
            type: 'BBBBBB',
            description: 'BBBBBB',
            customerRef: 'BBBBBB',
            gvCode: 'BBBBBB',
            endToEnd: 'BBBBBB',
            primanota: 'BBBBBB',
            creditor: 'BBBBBB',
            mandate: 'BBBBBB',
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

      it('should delete a BankTransaction', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
