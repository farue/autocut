import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { GlobalSetting, IGlobalSetting } from '../global-setting.model';

import { GlobalSettingService } from './global-setting.service';

describe('GlobalSetting Service', () => {
  let service: GlobalSettingService;
  let httpMock: HttpTestingController;
  let elemDefault: IGlobalSetting;
  let expectedResult: IGlobalSetting | IGlobalSetting[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GlobalSettingService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      key: 'AAAAAAA',
      value: 'AAAAAAA',
      valueType: 'AAAAAAA',
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

    it('should create a GlobalSetting', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new GlobalSetting()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GlobalSetting', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          key: 'BBBBBB',
          value: 'BBBBBB',
          valueType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GlobalSetting', () => {
      const patchObject = Object.assign(
        {
          value: 'BBBBBB',
          valueType: 'BBBBBB',
        },
        new GlobalSetting()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GlobalSetting', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          key: 'BBBBBB',
          value: 'BBBBBB',
          valueType: 'BBBBBB',
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

    it('should delete a GlobalSetting', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGlobalSettingToCollectionIfMissing', () => {
      it('should add a GlobalSetting to an empty array', () => {
        const globalSetting: IGlobalSetting = { id: 123 };
        expectedResult = service.addGlobalSettingToCollectionIfMissing([], globalSetting);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(globalSetting);
      });

      it('should not add a GlobalSetting to an array that contains it', () => {
        const globalSetting: IGlobalSetting = { id: 123 };
        const globalSettingCollection: IGlobalSetting[] = [
          {
            ...globalSetting,
          },
          { id: 456 },
        ];
        expectedResult = service.addGlobalSettingToCollectionIfMissing(globalSettingCollection, globalSetting);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GlobalSetting to an array that doesn't contain it", () => {
        const globalSetting: IGlobalSetting = { id: 123 };
        const globalSettingCollection: IGlobalSetting[] = [{ id: 456 }];
        expectedResult = service.addGlobalSettingToCollectionIfMissing(globalSettingCollection, globalSetting);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(globalSetting);
      });

      it('should add only unique GlobalSetting to an array', () => {
        const globalSettingArray: IGlobalSetting[] = [{ id: 123 }, { id: 456 }, { id: 70440 }];
        const globalSettingCollection: IGlobalSetting[] = [{ id: 123 }];
        expectedResult = service.addGlobalSettingToCollectionIfMissing(globalSettingCollection, ...globalSettingArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const globalSetting: IGlobalSetting = { id: 123 };
        const globalSetting2: IGlobalSetting = { id: 456 };
        expectedResult = service.addGlobalSettingToCollectionIfMissing([], globalSetting, globalSetting2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(globalSetting);
        expect(expectedResult).toContain(globalSetting2);
      });

      it('should accept null and undefined values', () => {
        const globalSetting: IGlobalSetting = { id: 123 };
        expectedResult = service.addGlobalSettingToCollectionIfMissing([], null, globalSetting, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(globalSetting);
      });

      it('should return initial array if no GlobalSetting is added', () => {
        const globalSettingCollection: IGlobalSetting[] = [{ id: 123 }];
        expectedResult = service.addGlobalSettingToCollectionIfMissing(globalSettingCollection, undefined, null);
        expect(expectedResult).toEqual(globalSettingCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
