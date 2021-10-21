import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import {DATE_TIME_FORMAT} from 'app/config/input.constants';
import {INetworkSwitchStatus, NetworkSwitchStatus} from '../network-switch-status.model';

import {NetworkSwitchStatusService} from './network-switch-status.service';

describe('NetworkSwitchStatus Service', () => {
  let service: NetworkSwitchStatusService;
  let httpMock: HttpTestingController;
  let elemDefault: INetworkSwitchStatus;
  let expectedResult: INetworkSwitchStatus | INetworkSwitchStatus[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NetworkSwitchStatusService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      port: 'AAAAAAA',
      name: 'AAAAAAA',
      status: 'AAAAAAA',
      vlan: 'AAAAAAA',
      speed: 'AAAAAAA',
      type: 'AAAAAAA',
      timestamp: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a NetworkSwitchStatus', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.create(new NetworkSwitchStatus()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a NetworkSwitchStatus', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          port: 'BBBBBB',
          name: 'BBBBBB',
          status: 'BBBBBB',
          vlan: 'BBBBBB',
          speed: 'BBBBBB',
          type: 'BBBBBB',
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a NetworkSwitchStatus', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          status: 'BBBBBB',
          vlan: 'BBBBBB',
          speed: 'BBBBBB',
          type: 'BBBBBB',
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        new NetworkSwitchStatus()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of NetworkSwitchStatus', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          port: 'BBBBBB',
          name: 'BBBBBB',
          status: 'BBBBBB',
          vlan: 'BBBBBB',
          speed: 'BBBBBB',
          type: 'BBBBBB',
          timestamp: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          timestamp: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a NetworkSwitchStatus', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNetworkSwitchStatusToCollectionIfMissing', () => {
      it('should add a NetworkSwitchStatus to an empty array', () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 123 };
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing([], networkSwitchStatus);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(networkSwitchStatus);
      });

      it('should not add a NetworkSwitchStatus to an array that contains it', () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 123 };
        const networkSwitchStatusCollection: INetworkSwitchStatus[] = [
          {
            ...networkSwitchStatus,
          },
          { id: 456 },
        ];
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing(networkSwitchStatusCollection, networkSwitchStatus);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a NetworkSwitchStatus to an array that doesn't contain it", () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 123 };
        const networkSwitchStatusCollection: INetworkSwitchStatus[] = [{ id: 456 }];
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing(networkSwitchStatusCollection, networkSwitchStatus);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(networkSwitchStatus);
      });

      it('should add only unique NetworkSwitchStatus to an array', () => {
        const networkSwitchStatusArray: INetworkSwitchStatus[] = [{ id: 123 }, { id: 456 }, { id: 32332 }];
        const networkSwitchStatusCollection: INetworkSwitchStatus[] = [{ id: 123 }];
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing(networkSwitchStatusCollection, ...networkSwitchStatusArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 123 };
        const networkSwitchStatus2: INetworkSwitchStatus = { id: 456 };
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing([], networkSwitchStatus, networkSwitchStatus2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(networkSwitchStatus);
        expect(expectedResult).toContain(networkSwitchStatus2);
      });

      it('should accept null and undefined values', () => {
        const networkSwitchStatus: INetworkSwitchStatus = { id: 123 };
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing([], null, networkSwitchStatus, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(networkSwitchStatus);
      });

      it('should return initial array if no NetworkSwitchStatus is added', () => {
        const networkSwitchStatusCollection: INetworkSwitchStatus[] = [{ id: 123 }];
        expectedResult = service.addNetworkSwitchStatusToCollectionIfMissing(networkSwitchStatusCollection, undefined, null);
        expect(expectedResult).toEqual(networkSwitchStatusCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
