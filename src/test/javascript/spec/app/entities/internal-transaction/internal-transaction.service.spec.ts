import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { InternalTransactionService } from 'app/entities/internal-transaction/internal-transaction.service';
import { IInternalTransaction, InternalTransaction } from 'app/shared/model/internal-transaction.model';

describe('Service Tests', () => {
  describe('InternalTransaction Service', () => {
    let injector: TestBed;
    let service: InternalTransactionService;
    let httpMock: HttpTestingController;
    let elemDefault: IInternalTransaction;
    let expectedResult: IInternalTransaction | IInternalTransaction[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(InternalTransactionService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new InternalTransaction(0, 'AAAAAAA', currentDate, currentDate, 0, 0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA');
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

      it('should return a list of InternalTransaction', () => {
        const returnedFromService = Object.assign(
          {
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
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
