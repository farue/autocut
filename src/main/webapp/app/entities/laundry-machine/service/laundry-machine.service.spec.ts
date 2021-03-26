import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { LaundryMachineType } from 'app/entities/enumerations/laundry-machine-type.model';
import { ILaundryMachine, LaundryMachine } from '../laundry-machine.model';

import { LaundryMachineService } from './laundry-machine.service';

describe('Service Tests', () => {
  describe('LaundryMachine Service', () => {
    let service: LaundryMachineService;
    let httpMock: HttpTestingController;
    let elemDefault: ILaundryMachine;
    let expectedResult: ILaundryMachine | ILaundryMachine[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(LaundryMachineService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        identifier: 'AAAAAAA',
        name: 'AAAAAAA',
        type: LaundryMachineType.WASHING_MACHINE,
        enabled: false,
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

      it('should create a LaundryMachine', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new LaundryMachine()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a LaundryMachine', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            identifier: 'BBBBBB',
            name: 'BBBBBB',
            type: 'BBBBBB',
            enabled: true,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a LaundryMachine', () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
            enabled: true,
          },
          new LaundryMachine()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of LaundryMachine', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            identifier: 'BBBBBB',
            name: 'BBBBBB',
            type: 'BBBBBB',
            enabled: true,
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

      it('should delete a LaundryMachine', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addLaundryMachineToCollectionIfMissing', () => {
        it('should add a LaundryMachine to an empty array', () => {
          const laundryMachine: ILaundryMachine = { id: 123 };
          expectedResult = service.addLaundryMachineToCollectionIfMissing([], laundryMachine);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(laundryMachine);
        });

        it('should not add a LaundryMachine to an array that contains it', () => {
          const laundryMachine: ILaundryMachine = { id: 123 };
          const laundryMachineCollection: ILaundryMachine[] = [
            {
              ...laundryMachine,
            },
            { id: 456 },
          ];
          expectedResult = service.addLaundryMachineToCollectionIfMissing(laundryMachineCollection, laundryMachine);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a LaundryMachine to an array that doesn't contain it", () => {
          const laundryMachine: ILaundryMachine = { id: 123 };
          const laundryMachineCollection: ILaundryMachine[] = [{ id: 456 }];
          expectedResult = service.addLaundryMachineToCollectionIfMissing(laundryMachineCollection, laundryMachine);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(laundryMachine);
        });

        it('should add only unique LaundryMachine to an array', () => {
          const laundryMachineArray: ILaundryMachine[] = [{ id: 123 }, { id: 456 }, { id: 29067 }];
          const laundryMachineCollection: ILaundryMachine[] = [{ id: 123 }];
          expectedResult = service.addLaundryMachineToCollectionIfMissing(laundryMachineCollection, ...laundryMachineArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const laundryMachine: ILaundryMachine = { id: 123 };
          const laundryMachine2: ILaundryMachine = { id: 456 };
          expectedResult = service.addLaundryMachineToCollectionIfMissing([], laundryMachine, laundryMachine2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(laundryMachine);
          expect(expectedResult).toContain(laundryMachine2);
        });

        it('should accept null and undefined values', () => {
          const laundryMachine: ILaundryMachine = { id: 123 };
          expectedResult = service.addLaundryMachineToCollectionIfMissing([], null, laundryMachine, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(laundryMachine);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
