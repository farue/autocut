import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IInternetAccess, InternetAccess } from '../internet-access.model';

import { InternetAccessService } from './internet-access.service';

describe('Service Tests', () => {
  describe('InternetAccess Service', () => {
    let service: InternetAccessService;
    let httpMock: HttpTestingController;
    let elemDefault: IInternetAccess;
    let expectedResult: IInternetAccess | IInternetAccess[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(InternetAccessService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        blocked: false,
        ip1: 'AAAAAAA',
        ip2: 'AAAAAAA',
        switchInterface: 'AAAAAAA',
        port: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a InternetAccess', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new InternetAccess()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a InternetAccess', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            blocked: true,
            ip1: 'BBBBBB',
            ip2: 'BBBBBB',
            switchInterface: 'BBBBBB',
            port: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a InternetAccess', () => {
        const patchObject = Object.assign(
          {
            blocked: true,
            ip2: 'BBBBBB',
          },
          new InternetAccess()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of InternetAccess', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            blocked: true,
            ip1: 'BBBBBB',
            ip2: 'BBBBBB',
            switchInterface: 'BBBBBB',
            port: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a InternetAccess', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addInternetAccessToCollectionIfMissing', () => {
        it('should add a InternetAccess to an empty array', () => {
          const internetAccess: IInternetAccess = { id: 123 };
          expectedResult = service.addInternetAccessToCollectionIfMissing([], internetAccess);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(internetAccess);
        });

        it('should not add a InternetAccess to an array that contains it', () => {
          const internetAccess: IInternetAccess = { id: 123 };
          const internetAccessCollection: IInternetAccess[] = [
            {
              ...internetAccess,
            },
            { id: 456 },
          ];
          expectedResult = service.addInternetAccessToCollectionIfMissing(internetAccessCollection, internetAccess);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a InternetAccess to an array that doesn't contain it", () => {
          const internetAccess: IInternetAccess = { id: 123 };
          const internetAccessCollection: IInternetAccess[] = [{ id: 456 }];
          expectedResult = service.addInternetAccessToCollectionIfMissing(internetAccessCollection, internetAccess);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(internetAccess);
        });

        it('should add only unique InternetAccess to an array', () => {
          const internetAccessArray: IInternetAccess[] = [{ id: 123 }, { id: 456 }, { id: 62951 }];
          const internetAccessCollection: IInternetAccess[] = [{ id: 123 }];
          expectedResult = service.addInternetAccessToCollectionIfMissing(internetAccessCollection, ...internetAccessArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const internetAccess: IInternetAccess = { id: 123 };
          const internetAccess2: IInternetAccess = { id: 456 };
          expectedResult = service.addInternetAccessToCollectionIfMissing([], internetAccess, internetAccess2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(internetAccess);
          expect(expectedResult).toContain(internetAccess2);
        });

        it('should accept null and undefined values', () => {
          const internetAccess: IInternetAccess = { id: 123 };
          expectedResult = service.addInternetAccessToCollectionIfMissing([], null, internetAccess, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(internetAccess);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
