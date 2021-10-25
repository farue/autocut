import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ProtectionUnits } from 'app/entities/enumerations/protection-units.model';
import { Access } from 'app/entities/enumerations/access.model';
import { ISecurityPolicy, SecurityPolicy } from '../security-policy.model';

import { SecurityPolicyService } from './security-policy.service';

describe('SecurityPolicy Service', () => {
  let service: SecurityPolicyService;
  let httpMock: HttpTestingController;
  let elemDefault: ISecurityPolicy;
  let expectedResult: ISecurityPolicy | ISecurityPolicy[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SecurityPolicyService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      protectionUnit: ProtectionUnits.BANK_TRANSACTIONS,
      access: Access.READ_ALLOW,
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

    it('should create a SecurityPolicy', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new SecurityPolicy()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SecurityPolicy', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          protectionUnit: 'BBBBBB',
          access: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SecurityPolicy', () => {
      const patchObject = Object.assign(
        {
          protectionUnit: 'BBBBBB',
          access: 'BBBBBB',
        },
        new SecurityPolicy()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SecurityPolicy', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          protectionUnit: 'BBBBBB',
          access: 'BBBBBB',
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

    it('should delete a SecurityPolicy', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSecurityPolicyToCollectionIfMissing', () => {
      it('should add a SecurityPolicy to an empty array', () => {
        const securityPolicy: ISecurityPolicy = { id: 123 };
        expectedResult = service.addSecurityPolicyToCollectionIfMissing([], securityPolicy);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(securityPolicy);
      });

      it('should not add a SecurityPolicy to an array that contains it', () => {
        const securityPolicy: ISecurityPolicy = { id: 123 };
        const securityPolicyCollection: ISecurityPolicy[] = [
          {
            ...securityPolicy,
          },
          { id: 456 },
        ];
        expectedResult = service.addSecurityPolicyToCollectionIfMissing(securityPolicyCollection, securityPolicy);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SecurityPolicy to an array that doesn't contain it", () => {
        const securityPolicy: ISecurityPolicy = { id: 123 };
        const securityPolicyCollection: ISecurityPolicy[] = [{ id: 456 }];
        expectedResult = service.addSecurityPolicyToCollectionIfMissing(securityPolicyCollection, securityPolicy);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(securityPolicy);
      });

      it('should add only unique SecurityPolicy to an array', () => {
        const securityPolicyArray: ISecurityPolicy[] = [{ id: 123 }, { id: 456 }, { id: 27766 }];
        const securityPolicyCollection: ISecurityPolicy[] = [{ id: 123 }];
        expectedResult = service.addSecurityPolicyToCollectionIfMissing(securityPolicyCollection, ...securityPolicyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const securityPolicy: ISecurityPolicy = { id: 123 };
        const securityPolicy2: ISecurityPolicy = { id: 456 };
        expectedResult = service.addSecurityPolicyToCollectionIfMissing([], securityPolicy, securityPolicy2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(securityPolicy);
        expect(expectedResult).toContain(securityPolicy2);
      });

      it('should accept null and undefined values', () => {
        const securityPolicy: ISecurityPolicy = { id: 123 };
        expectedResult = service.addSecurityPolicyToCollectionIfMissing([], null, securityPolicy, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(securityPolicy);
      });

      it('should return initial array if no SecurityPolicy is added', () => {
        const securityPolicyCollection: ISecurityPolicy[] = [{ id: 123 }];
        expectedResult = service.addSecurityPolicyToCollectionIfMissing(securityPolicyCollection, undefined, null);
        expect(expectedResult).toEqual(securityPolicyCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
