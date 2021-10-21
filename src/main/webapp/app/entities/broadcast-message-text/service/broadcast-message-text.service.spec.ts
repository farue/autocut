import {TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {BroadcastMessageText, IBroadcastMessageText} from '../broadcast-message-text.model';

import {BroadcastMessageTextService} from './broadcast-message-text.service';

describe('BroadcastMessageText Service', () => {
  let service: BroadcastMessageTextService;
  let httpMock: HttpTestingController;
  let elemDefault: IBroadcastMessageText;
  let expectedResult: IBroadcastMessageText | IBroadcastMessageText[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BroadcastMessageTextService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      langKey: 'AAAAAAA',
      text: 'AAAAAAA',
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

    it('should create a BroadcastMessageText', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new BroadcastMessageText()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BroadcastMessageText', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          langKey: 'BBBBBB',
          text: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BroadcastMessageText', () => {
      const patchObject = Object.assign({}, new BroadcastMessageText());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BroadcastMessageText', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          langKey: 'BBBBBB',
          text: 'BBBBBB',
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

    it('should delete a BroadcastMessageText', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addBroadcastMessageTextToCollectionIfMissing', () => {
      it('should add a BroadcastMessageText to an empty array', () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 123 };
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing([], broadcastMessageText);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(broadcastMessageText);
      });

      it('should not add a BroadcastMessageText to an array that contains it', () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 123 };
        const broadcastMessageTextCollection: IBroadcastMessageText[] = [
          {
            ...broadcastMessageText,
          },
          { id: 456 },
        ];
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing(broadcastMessageTextCollection, broadcastMessageText);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BroadcastMessageText to an array that doesn't contain it", () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 123 };
        const broadcastMessageTextCollection: IBroadcastMessageText[] = [{ id: 456 }];
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing(broadcastMessageTextCollection, broadcastMessageText);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(broadcastMessageText);
      });

      it('should add only unique BroadcastMessageText to an array', () => {
        const broadcastMessageTextArray: IBroadcastMessageText[] = [{ id: 123 }, { id: 456 }, { id: 6632 }];
        const broadcastMessageTextCollection: IBroadcastMessageText[] = [{ id: 123 }];
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing(broadcastMessageTextCollection, ...broadcastMessageTextArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 123 };
        const broadcastMessageText2: IBroadcastMessageText = { id: 456 };
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing([], broadcastMessageText, broadcastMessageText2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(broadcastMessageText);
        expect(expectedResult).toContain(broadcastMessageText2);
      });

      it('should accept null and undefined values', () => {
        const broadcastMessageText: IBroadcastMessageText = { id: 123 };
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing([], null, broadcastMessageText, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(broadcastMessageText);
      });

      it('should return initial array if no BroadcastMessageText is added', () => {
        const broadcastMessageTextCollection: IBroadcastMessageText[] = [{ id: 123 }];
        expectedResult = service.addBroadcastMessageTextToCollectionIfMissing(broadcastMessageTextCollection, undefined, null);
        expect(expectedResult).toEqual(broadcastMessageTextCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
