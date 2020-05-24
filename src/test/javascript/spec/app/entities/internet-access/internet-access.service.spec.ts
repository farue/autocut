import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';
import { IInternetAccess, InternetAccess } from 'app/shared/model/internet-access.model';

describe('Service Tests', () => {
  describe('InternetAccess Service', () => {
    let injector: TestBed;
    let service: InternetAccessService;
    let httpMock: HttpTestingController;
    let elemDefault: IInternetAccess;
    let expectedResult: IInternetAccess | IInternetAccess[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(InternetAccessService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new InternetAccess(0, false, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 0);
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

      it('should return a list of InternetAccess', () => {
        const returnedFromService = Object.assign(
          {
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
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
