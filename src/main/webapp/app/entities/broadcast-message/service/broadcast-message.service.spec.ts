import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { BroadcastMessageType } from 'app/entities/enumerations/broadcast-message-type.model';
import { BroadcastMessage, IBroadcastMessage } from '../broadcast-message.model';

import { BroadcastMessageService } from './broadcast-message.service';

describe('BroadcastMessage Service', () => {
  let service: BroadcastMessageService;
  let httpMock: HttpTestingController;
  let elemDefault: IBroadcastMessage;
  let expectedResult: IBroadcastMessage | IBroadcastMessage[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BroadcastMessageService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      type: BroadcastMessageType.PRIMARY,
      start: currentDate,
      end: currentDate,
      usersOnly: false,
      dismissible: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          start: currentDate.format(DATE_TIME_FORMAT),
          end: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a BroadcastMessage', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          start: currentDate.format(DATE_TIME_FORMAT),
          end: currentDate.format(DATE_TIME_FORMAT),
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

      service.create(new BroadcastMessage()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BroadcastMessage', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
          start: currentDate.format(DATE_TIME_FORMAT),
          end: currentDate.format(DATE_TIME_FORMAT),
          usersOnly: true,
          dismissible: true,
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

    it('should partial update a BroadcastMessage', () => {
      const patchObject = Object.assign(
        {
          type: 'BBBBBB',
          start: currentDate.format(DATE_TIME_FORMAT),
          usersOnly: true,
        },
        new BroadcastMessage()
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

    it('should return a list of BroadcastMessage', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          type: 'BBBBBB',
          start: currentDate.format(DATE_TIME_FORMAT),
          end: currentDate.format(DATE_TIME_FORMAT),
          usersOnly: true,
          dismissible: true,
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

    it('should delete a BroadcastMessage', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBroadcastMessageToCollectionIfMissing', () => {
      it('should add a BroadcastMessage to an empty array', () => {
        const broadcastMessage: IBroadcastMessage = { id: 123 };
        expectedResult = service.addBroadcastMessageToCollectionIfMissing([], broadcastMessage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(broadcastMessage);
      });

      it('should not add a BroadcastMessage to an array that contains it', () => {
        const broadcastMessage: IBroadcastMessage = { id: 123 };
        const broadcastMessageCollection: IBroadcastMessage[] = [
          {
            ...broadcastMessage,
          },
          { id: 456 },
        ];
        expectedResult = service.addBroadcastMessageToCollectionIfMissing(broadcastMessageCollection, broadcastMessage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BroadcastMessage to an array that doesn't contain it", () => {
        const broadcastMessage: IBroadcastMessage = { id: 123 };
        const broadcastMessageCollection: IBroadcastMessage[] = [{ id: 456 }];
        expectedResult = service.addBroadcastMessageToCollectionIfMissing(broadcastMessageCollection, broadcastMessage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(broadcastMessage);
      });

      it('should add only unique BroadcastMessage to an array', () => {
        const broadcastMessageArray: IBroadcastMessage[] = [{ id: 123 }, { id: 456 }, { id: 56293 }];
        const broadcastMessageCollection: IBroadcastMessage[] = [{ id: 123 }];
        expectedResult = service.addBroadcastMessageToCollectionIfMissing(broadcastMessageCollection, ...broadcastMessageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const broadcastMessage: IBroadcastMessage = { id: 123 };
        const broadcastMessage2: IBroadcastMessage = { id: 456 };
        expectedResult = service.addBroadcastMessageToCollectionIfMissing([], broadcastMessage, broadcastMessage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(broadcastMessage);
        expect(expectedResult).toContain(broadcastMessage2);
      });

      it('should accept null and undefined values', () => {
        const broadcastMessage: IBroadcastMessage = { id: 123 };
        expectedResult = service.addBroadcastMessageToCollectionIfMissing([], null, broadcastMessage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(broadcastMessage);
      });

      it('should return initial array if no BroadcastMessage is added', () => {
        const broadcastMessageCollection: IBroadcastMessage[] = [{ id: 123 }];
        expectedResult = service.addBroadcastMessageToCollectionIfMissing(broadcastMessageCollection, undefined, null);
        expect(expectedResult).toEqual(broadcastMessageCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
