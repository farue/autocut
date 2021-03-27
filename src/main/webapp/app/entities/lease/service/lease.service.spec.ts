import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILease, Lease } from '../lease.model';

import { LeaseService } from './lease.service';

describe('Service Tests', () => {
  describe('Lease Service', () => {
    let service: LeaseService;
    let httpMock: HttpTestingController;
    let elemDefault: ILease;
    let expectedResult: ILease | ILease[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(LeaseService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        nr: 'AAAAAAA',
        start: currentDate,
        end: currentDate,
        blocked: false,
        pictureContractContentType: 'image/png',
        pictureContract: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            start: currentDate.format(DATE_FORMAT),
            end: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Lease', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            start: currentDate.format(DATE_FORMAT),
            end: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.create(new Lease()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Lease', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            nr: 'BBBBBB',
            start: currentDate.format(DATE_FORMAT),
            end: currentDate.format(DATE_FORMAT),
            blocked: true,
            pictureContract: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Lease', () => {
        const patchObject = Object.assign(
          {
            nr: 'BBBBBB',
            start: currentDate.format(DATE_FORMAT),
            pictureContract: 'BBBBBB',
          },
          new Lease()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Lease', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            nr: 'BBBBBB',
            start: currentDate.format(DATE_FORMAT),
            end: currentDate.format(DATE_FORMAT),
            blocked: true,
            pictureContract: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

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

      describe('addLeaseToCollectionIfMissing', () => {
        it('should add a Lease to an empty array', () => {
          const lease: ILease = { id: 123 };
          expectedResult = service.addLeaseToCollectionIfMissing([], lease);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(lease);
        });

        it('should not add a Lease to an array that contains it', () => {
          const lease: ILease = { id: 123 };
          const leaseCollection: ILease[] = [
            {
              ...lease,
            },
            { id: 456 },
          ];
          expectedResult = service.addLeaseToCollectionIfMissing(leaseCollection, lease);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Lease to an array that doesn't contain it", () => {
          const lease: ILease = { id: 123 };
          const leaseCollection: ILease[] = [{ id: 456 }];
          expectedResult = service.addLeaseToCollectionIfMissing(leaseCollection, lease);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(lease);
        });

        it('should add only unique Lease to an array', () => {
          const leaseArray: ILease[] = [{ id: 123 }, { id: 456 }, { id: 76585 }];
          const leaseCollection: ILease[] = [{ id: 123 }];
          expectedResult = service.addLeaseToCollectionIfMissing(leaseCollection, ...leaseArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const lease: ILease = { id: 123 };
          const lease2: ILease = { id: 456 };
          expectedResult = service.addLeaseToCollectionIfMissing([], lease, lease2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(lease);
          expect(expectedResult).toContain(lease2);
        });

        it('should accept null and undefined values', () => {
          const lease: ILease = { id: 123 };
          expectedResult = service.addLeaseToCollectionIfMissing([], null, lease, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(lease);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
