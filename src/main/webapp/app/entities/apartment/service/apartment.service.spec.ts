import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ApartmentTypes } from 'app/entities/enumerations/apartment-types.model';
import { Apartment, IApartment } from '../apartment.model';

import { ApartmentService } from './apartment.service';

describe('Apartment Service', () => {
  let service: ApartmentService;
  let httpMock: HttpTestingController;
  let elemDefault: IApartment;
  let expectedResult: IApartment | IApartment[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ApartmentService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nr: 'AAAAAAA',
      type: ApartmentTypes.SHARED,
      maxNumberOfLeases: 0,
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

    it('should create a Apartment', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Apartment()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Apartment', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nr: 'BBBBBB',
          type: 'BBBBBB',
          maxNumberOfLeases: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Apartment', () => {
      const patchObject = Object.assign(
        {
          nr: 'BBBBBB',
          type: 'BBBBBB',
        },
        new Apartment()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Apartment', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nr: 'BBBBBB',
          type: 'BBBBBB',
          maxNumberOfLeases: 1,
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

    it('should delete a Apartment', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addApartmentToCollectionIfMissing', () => {
      it('should add a Apartment to an empty array', () => {
        const apartment: IApartment = { id: 123 };
        expectedResult = service.addApartmentToCollectionIfMissing([], apartment);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(apartment);
      });

      it('should not add a Apartment to an array that contains it', () => {
        const apartment: IApartment = { id: 123 };
        const apartmentCollection: IApartment[] = [
          {
            ...apartment,
          },
          { id: 456 },
        ];
        expectedResult = service.addApartmentToCollectionIfMissing(apartmentCollection, apartment);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Apartment to an array that doesn't contain it", () => {
        const apartment: IApartment = { id: 123 };
        const apartmentCollection: IApartment[] = [{ id: 456 }];
        expectedResult = service.addApartmentToCollectionIfMissing(apartmentCollection, apartment);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(apartment);
      });

      it('should add only unique Apartment to an array', () => {
        const apartmentArray: IApartment[] = [{ id: 123 }, { id: 456 }, { id: 8626 }];
        const apartmentCollection: IApartment[] = [{ id: 123 }];
        expectedResult = service.addApartmentToCollectionIfMissing(apartmentCollection, ...apartmentArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const apartment: IApartment = { id: 123 };
        const apartment2: IApartment = { id: 456 };
        expectedResult = service.addApartmentToCollectionIfMissing([], apartment, apartment2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(apartment);
        expect(expectedResult).toContain(apartment2);
      });

      it('should accept null and undefined values', () => {
        const apartment: IApartment = { id: 123 };
        expectedResult = service.addApartmentToCollectionIfMissing([], null, apartment, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(apartment);
      });

      it('should return initial array if no Apartment is added', () => {
        const apartmentCollection: IApartment[] = [{ id: 123 }];
        expectedResult = service.addApartmentToCollectionIfMissing(apartmentCollection, undefined, null);
        expect(expectedResult).toEqual(apartmentCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
