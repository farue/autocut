import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import {DATE_TIME_FORMAT} from 'app/config/input.constants';
import {WashHistoryStatus} from 'app/entities/enumerations/wash-history-status.model';
import {IWashHistory, WashHistory} from '../wash-history.model';

import {WashHistoryService} from './wash-history.service';

describe('WashHistory Service', () => {
  let service: WashHistoryService;
  let httpMock: HttpTestingController;
  let elemDefault: IWashHistory;
  let expectedResult: IWashHistory | IWashHistory[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(WashHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      usingDate: currentDate,
      reservationDate: currentDate,
      lastModifiedDate: currentDate,
      status: WashHistoryStatus.OPEN,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          usingDate: currentDate.format(DATE_TIME_FORMAT),
          reservationDate: currentDate.format(DATE_TIME_FORMAT),
          lastModifiedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a WashHistory', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          usingDate: currentDate.format(DATE_TIME_FORMAT),
          reservationDate: currentDate.format(DATE_TIME_FORMAT),
          lastModifiedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          usingDate: currentDate,
          reservationDate: currentDate,
          lastModifiedDate: currentDate,
        },
        returnedFromService
      );

      service.create(new WashHistory()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WashHistory', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          usingDate: currentDate.format(DATE_TIME_FORMAT),
          reservationDate: currentDate.format(DATE_TIME_FORMAT),
          lastModifiedDate: currentDate.format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          usingDate: currentDate,
          reservationDate: currentDate,
          lastModifiedDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WashHistory', () => {
      const patchObject = Object.assign(
        {
          usingDate: currentDate.format(DATE_TIME_FORMAT),
          lastModifiedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        new WashHistory()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          usingDate: currentDate,
          reservationDate: currentDate,
          lastModifiedDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WashHistory', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          usingDate: currentDate.format(DATE_TIME_FORMAT),
          reservationDate: currentDate.format(DATE_TIME_FORMAT),
          lastModifiedDate: currentDate.format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          usingDate: currentDate,
          reservationDate: currentDate,
          lastModifiedDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a WashHistory', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addWashHistoryToCollectionIfMissing', () => {
      it('should add a WashHistory to an empty array', () => {
        const washHistory: IWashHistory = { id: 123 };
        expectedResult = service.addWashHistoryToCollectionIfMissing([], washHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(washHistory);
      });

      it('should not add a WashHistory to an array that contains it', () => {
        const washHistory: IWashHistory = { id: 123 };
        const washHistoryCollection: IWashHistory[] = [
          {
            ...washHistory,
          },
          { id: 456 },
        ];
        expectedResult = service.addWashHistoryToCollectionIfMissing(washHistoryCollection, washHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WashHistory to an array that doesn't contain it", () => {
        const washHistory: IWashHistory = { id: 123 };
        const washHistoryCollection: IWashHistory[] = [{ id: 456 }];
        expectedResult = service.addWashHistoryToCollectionIfMissing(washHistoryCollection, washHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(washHistory);
      });

      it('should add only unique WashHistory to an array', () => {
        const washHistoryArray: IWashHistory[] = [{ id: 123 }, { id: 456 }, { id: 23590 }];
        const washHistoryCollection: IWashHistory[] = [{ id: 123 }];
        expectedResult = service.addWashHistoryToCollectionIfMissing(washHistoryCollection, ...washHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const washHistory: IWashHistory = { id: 123 };
        const washHistory2: IWashHistory = { id: 456 };
        expectedResult = service.addWashHistoryToCollectionIfMissing([], washHistory, washHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(washHistory);
        expect(expectedResult).toContain(washHistory2);
      });

      it('should accept null and undefined values', () => {
        const washHistory: IWashHistory = { id: 123 };
        expectedResult = service.addWashHistoryToCollectionIfMissing([], null, washHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(washHistory);
      });

      it('should return initial array if no WashHistory is added', () => {
        const washHistoryCollection: IWashHistory[] = [{ id: 123 }];
        expectedResult = service.addWashHistoryToCollectionIfMissing(washHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(washHistoryCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
