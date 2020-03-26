import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { CoinService } from 'app/entities/coin/coin.service';
import { ICoin, Coin } from 'app/shared/model/coin.model';

describe('Service Tests', () => {
  describe('Coin Service', () => {
    let injector: TestBed;
    let service: CoinService;
    let httpMock: HttpTestingController;
    let elemDefault: ICoin;
    let expectedResult: ICoin | ICoin[] | boolean | null;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(CoinService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Coin(0, 'AAAAAAA', currentDate, currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            datePurchase: currentDate.format(DATE_TIME_FORMAT),
            dateRedeem: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Coin', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            datePurchase: currentDate.format(DATE_TIME_FORMAT),
            dateRedeem: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            datePurchase: currentDate,
            dateRedeem: currentDate
          },
          returnedFromService
        );
        service
          .create(new Coin())
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Coin', () => {
        const returnedFromService = Object.assign(
          {
            token: 'BBBBBB',
            datePurchase: currentDate.format(DATE_TIME_FORMAT),
            dateRedeem: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            datePurchase: currentDate,
            dateRedeem: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Coin', () => {
        const returnedFromService = Object.assign(
          {
            token: 'BBBBBB',
            datePurchase: currentDate.format(DATE_TIME_FORMAT),
            dateRedeem: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            datePurchase: currentDate,
            dateRedeem: currentDate
          },
          returnedFromService
        );
        service
          .query()
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Coin', () => {
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
