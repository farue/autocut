import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SecurityPolicyService } from 'app/entities/security-policy/security-policy.service';
import { ISecurityPolicy, SecurityPolicy } from 'app/shared/model/security-policy.model';
import { ProtectionUnits } from 'app/shared/model/enumerations/protection-units.model';
import { Access } from 'app/shared/model/enumerations/access.model';

describe('Service Tests', () => {
  describe('SecurityPolicy Service', () => {
    let injector: TestBed;
    let service: SecurityPolicyService;
    let httpMock: HttpTestingController;
    let elemDefault: ISecurityPolicy;
    let expectedResult: ISecurityPolicy | ISecurityPolicy[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(SecurityPolicyService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new SecurityPolicy(0, ProtectionUnits.BANK_TRANSACTIONS, Access.READ_ALLOW);
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

      it('should return a list of SecurityPolicy', () => {
        const returnedFromService = Object.assign(
          {
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
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
