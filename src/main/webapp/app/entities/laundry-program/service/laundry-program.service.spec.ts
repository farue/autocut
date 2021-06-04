import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILaundryProgram, LaundryProgram } from '../laundry-program.model';

import { LaundryProgramService } from './laundry-program.service';

describe('Service Tests', () => {
  describe('LaundryProgram Service', () => {
    let service: LaundryProgramService;
    let httpMock: HttpTestingController;
    let elemDefault: ILaundryProgram;
    let expectedResult: ILaundryProgram | ILaundryProgram[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(LaundryProgramService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        subprogram: 'AAAAAAA',
        spin: 0,
        preWash: false,
        protect: false,
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

      it('should create a LaundryProgram', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new LaundryProgram()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a LaundryProgram', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            subprogram: 'BBBBBB',
            spin: 1,
            preWash: true,
            protect: true,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a LaundryProgram', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
            preWash: true,
            protect: true,
          },
          new LaundryProgram()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of LaundryProgram', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            subprogram: 'BBBBBB',
            spin: 1,
            preWash: true,
            protect: true,
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

      it('should delete a LaundryProgram', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addLaundryProgramToCollectionIfMissing', () => {
        it('should add a LaundryProgram to an empty array', () => {
          const laundryProgram: ILaundryProgram = { id: 123 };
          expectedResult = service.addLaundryProgramToCollectionIfMissing([], laundryProgram);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(laundryProgram);
        });

        it('should not add a LaundryProgram to an array that contains it', () => {
          const laundryProgram: ILaundryProgram = { id: 123 };
          const laundryProgramCollection: ILaundryProgram[] = [
            {
              ...laundryProgram,
            },
            { id: 456 },
          ];
          expectedResult = service.addLaundryProgramToCollectionIfMissing(laundryProgramCollection, laundryProgram);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a LaundryProgram to an array that doesn't contain it", () => {
          const laundryProgram: ILaundryProgram = { id: 123 };
          const laundryProgramCollection: ILaundryProgram[] = [{ id: 456 }];
          expectedResult = service.addLaundryProgramToCollectionIfMissing(laundryProgramCollection, laundryProgram);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(laundryProgram);
        });

        it('should add only unique LaundryProgram to an array', () => {
          const laundryProgramArray: ILaundryProgram[] = [{ id: 123 }, { id: 456 }, { id: 20202 }];
          const laundryProgramCollection: ILaundryProgram[] = [{ id: 123 }];
          expectedResult = service.addLaundryProgramToCollectionIfMissing(laundryProgramCollection, ...laundryProgramArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const laundryProgram: ILaundryProgram = { id: 123 };
          const laundryProgram2: ILaundryProgram = { id: 456 };
          expectedResult = service.addLaundryProgramToCollectionIfMissing([], laundryProgram, laundryProgram2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(laundryProgram);
          expect(expectedResult).toContain(laundryProgram2);
        });

        it('should accept null and undefined values', () => {
          const laundryProgram: ILaundryProgram = { id: 123 };
          expectedResult = service.addLaundryProgramToCollectionIfMissing([], null, laundryProgram, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(laundryProgram);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
