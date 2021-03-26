import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICommunication, Communication } from '../communication.model';

import { CommunicationService } from './communication.service';

describe('Service Tests', () => {
  describe('Communication Service', () => {
    let service: CommunicationService;
    let httpMock: HttpTestingController;
    let elemDefault: ICommunication;
    let expectedResult: ICommunication | ICommunication[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(CommunicationService);
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

      it('should create a Communication', () => {
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

        service.create(new Communication()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Communication', () => {
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

      it('should partial update a Communication', () => {
        const patchObject = Object.assign(
          {
            subject: 'BBBBBB',
            text: 'BBBBBB',
            note: 'BBBBBB',
            date: currentDate.format(DATE_TIME_FORMAT),
          },
          new Communication()
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

      it('should return a list of Communication', () => {
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

      it('should delete a Communication', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addCommunicationToCollectionIfMissing', () => {
        it('should add a Communication to an empty array', () => {
          const communication: ICommunication = { id: 123 };
          expectedResult = service.addCommunicationToCollectionIfMissing([], communication);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(communication);
        });

        it('should not add a Communication to an array that contains it', () => {
          const communication: ICommunication = { id: 123 };
          const communicationCollection: ICommunication[] = [
            {
              ...communication,
            },
            { id: 456 },
          ];
          expectedResult = service.addCommunicationToCollectionIfMissing(communicationCollection, communication);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Communication to an array that doesn't contain it", () => {
          const communication: ICommunication = { id: 123 };
          const communicationCollection: ICommunication[] = [{ id: 456 }];
          expectedResult = service.addCommunicationToCollectionIfMissing(communicationCollection, communication);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(communication);
        });

        it('should add only unique Communication to an array', () => {
          const communicationArray: ICommunication[] = [{ id: 123 }, { id: 456 }, { id: 76255 }];
          const communicationCollection: ICommunication[] = [{ id: 123 }];
          expectedResult = service.addCommunicationToCollectionIfMissing(communicationCollection, ...communicationArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const communication: ICommunication = { id: 123 };
          const communication2: ICommunication = { id: 456 };
          expectedResult = service.addCommunicationToCollectionIfMissing([], communication, communication2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(communication);
          expect(expectedResult).toContain(communication2);
        });

        it('should accept null and undefined values', () => {
          const communication: ICommunication = { id: 123 };
          expectedResult = service.addCommunicationToCollectionIfMissing([], null, communication, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(communication);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
