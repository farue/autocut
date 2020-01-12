import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { LeaseService } from 'app/entities/lease/lease.service';
import { ILease, Lease } from 'app/shared/model/lease.model';

describe('Service Tests', () => {
  describe('Lease Service', () => {
    let injector: TestBed;
    let service: LeaseService;
    let httpMock: HttpTestingController;
    let elemDefault: ILease;
    let expectedResult: ILease | ILease[] | boolean | null;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(LeaseService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new Lease(0, 'AAAAAAA', currentDate, currentDate, 'AAAAAAA', currentDate, 'AAAAAAA', currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
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

      it('should create a Lease', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
            createdDate: currentDate,
            lastModifiedDate: currentDate
          },
          returnedFromService
        );
        service
          .create(new Lease())
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp.body));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Lease', () => {
        const returnedFromService = Object.assign(
          {
            nr: 'BBBBBB',
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
            createdBy: 'BBBBBB',
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedBy: 'BBBBBB',
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
            createdDate: currentDate,
            lastModifiedDate: currentDate
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

      it('should return a list of Lease', () => {
        const returnedFromService = Object.assign(
          {
            nr: 'BBBBBB',
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
            createdBy: 'BBBBBB',
            createdDate: currentDate.format(DATE_TIME_FORMAT),
            lastModifiedBy: 'BBBBBB',
            lastModifiedDate: currentDate.format(DATE_TIME_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
            createdDate: currentDate,
            lastModifiedDate: currentDate
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

      it('should delete a Lease', () => {
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
