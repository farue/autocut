import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITenantCommunication, TenantCommunication } from '../tenant-communication.model';

import { TenantCommunicationService } from './tenant-communication.service';

describe('TenantCommunication Service', () => {
  let service: TenantCommunicationService;
  let httpMock: HttpTestingController;
  let elemDefault: ITenantCommunication;
  let expectedResult: ITenantCommunication | ITenantCommunication[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TenantCommunicationService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      subject: 'AAAAAAA',
      text: 'AAAAAAA',
      note: 'AAAAAAA',
      date: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a TenantCommunication', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.create(new TenantCommunication()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TenantCommunication', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          subject: 'BBBBBB',
          text: 'BBBBBB',
          note: 'BBBBBB',
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TenantCommunication', () => {
      const patchObject = Object.assign(
        {
          note: 'BBBBBB',
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        new TenantCommunication()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TenantCommunication', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          subject: 'BBBBBB',
          text: 'BBBBBB',
          note: 'BBBBBB',
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a TenantCommunication', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTenantCommunicationToCollectionIfMissing', () => {
      it('should add a TenantCommunication to an empty array', () => {
        const tenantCommunication: ITenantCommunication = { id: 123 };
        expectedResult = service.addTenantCommunicationToCollectionIfMissing([], tenantCommunication);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tenantCommunication);
      });

      it('should not add a TenantCommunication to an array that contains it', () => {
        const tenantCommunication: ITenantCommunication = { id: 123 };
        const tenantCommunicationCollection: ITenantCommunication[] = [
          {
            ...tenantCommunication,
          },
          { id: 456 },
        ];
        expectedResult = service.addTenantCommunicationToCollectionIfMissing(tenantCommunicationCollection, tenantCommunication);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TenantCommunication to an array that doesn't contain it", () => {
        const tenantCommunication: ITenantCommunication = { id: 123 };
        const tenantCommunicationCollection: ITenantCommunication[] = [{ id: 456 }];
        expectedResult = service.addTenantCommunicationToCollectionIfMissing(tenantCommunicationCollection, tenantCommunication);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenantCommunication);
      });

      it('should add only unique TenantCommunication to an array', () => {
        const tenantCommunicationArray: ITenantCommunication[] = [{ id: 123 }, { id: 456 }, { id: 91684 }];
        const tenantCommunicationCollection: ITenantCommunication[] = [{ id: 123 }];
        expectedResult = service.addTenantCommunicationToCollectionIfMissing(tenantCommunicationCollection, ...tenantCommunicationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tenantCommunication: ITenantCommunication = { id: 123 };
        const tenantCommunication2: ITenantCommunication = { id: 456 };
        expectedResult = service.addTenantCommunicationToCollectionIfMissing([], tenantCommunication, tenantCommunication2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tenantCommunication);
        expect(expectedResult).toContain(tenantCommunication2);
      });

      it('should accept null and undefined values', () => {
        const tenantCommunication: ITenantCommunication = { id: 123 };
        expectedResult = service.addTenantCommunicationToCollectionIfMissing([], null, tenantCommunication, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tenantCommunication);
      });

      it('should return initial array if no TenantCommunication is added', () => {
        const tenantCommunicationCollection: ITenantCommunication[] = [{ id: 123 }];
        expectedResult = service.addTenantCommunicationToCollectionIfMissing(tenantCommunicationCollection, undefined, null);
        expect(expectedResult).toEqual(tenantCommunicationCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
