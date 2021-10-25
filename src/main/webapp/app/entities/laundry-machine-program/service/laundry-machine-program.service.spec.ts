import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';

import { LaundryMachineProgramService } from './laundry-machine-program.service';

describe('LaundryMachineProgram Service', () => {
  let service: LaundryMachineProgramService;
  let httpMock: HttpTestingController;
  let elemDefault: ILaundryMachineProgram;
  let expectedResult: ILaundryMachineProgram | ILaundryMachineProgram[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LaundryMachineProgramService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      time: 0,
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

    it('should create a LaundryMachineProgram', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new LaundryMachineProgram()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LaundryMachineProgram', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          time: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LaundryMachineProgram', () => {
      const patchObject = Object.assign(
        {
          time: 1,
        },
        new LaundryMachineProgram()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LaundryMachineProgram', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          time: 1,
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

    it('should delete a LaundryMachineProgram', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLaundryMachineProgramToCollectionIfMissing', () => {
      it('should add a LaundryMachineProgram to an empty array', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 123 };
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing([], laundryMachineProgram);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(laundryMachineProgram);
      });

      it('should not add a LaundryMachineProgram to an array that contains it', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 123 };
        const laundryMachineProgramCollection: ILaundryMachineProgram[] = [
          {
            ...laundryMachineProgram,
          },
          { id: 456 },
        ];
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing(laundryMachineProgramCollection, laundryMachineProgram);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LaundryMachineProgram to an array that doesn't contain it", () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 123 };
        const laundryMachineProgramCollection: ILaundryMachineProgram[] = [{ id: 456 }];
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing(laundryMachineProgramCollection, laundryMachineProgram);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(laundryMachineProgram);
      });

      it('should add only unique LaundryMachineProgram to an array', () => {
        const laundryMachineProgramArray: ILaundryMachineProgram[] = [{ id: 123 }, { id: 456 }, { id: 39517 }];
        const laundryMachineProgramCollection: ILaundryMachineProgram[] = [{ id: 123 }];
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing(
          laundryMachineProgramCollection,
          ...laundryMachineProgramArray
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 123 };
        const laundryMachineProgram2: ILaundryMachineProgram = { id: 456 };
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing([], laundryMachineProgram, laundryMachineProgram2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(laundryMachineProgram);
        expect(expectedResult).toContain(laundryMachineProgram2);
      });

      it('should accept null and undefined values', () => {
        const laundryMachineProgram: ILaundryMachineProgram = { id: 123 };
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing([], null, laundryMachineProgram, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(laundryMachineProgram);
      });

      it('should return initial array if no LaundryMachineProgram is added', () => {
        const laundryMachineProgramCollection: ILaundryMachineProgram[] = [{ id: 123 }];
        expectedResult = service.addLaundryMachineProgramToCollectionIfMissing(laundryMachineProgramCollection, undefined, null);
        expect(expectedResult).toEqual(laundryMachineProgramCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
