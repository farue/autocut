import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/laundry-machine-program.service';
import { ILaundryMachineProgram, LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

describe('Service Tests', () => {
  describe('LaundryMachineProgram Service', () => {
    let injector: TestBed;
    let service: LaundryMachineProgramService;
    let httpMock: HttpTestingController;
    let elemDefault: ILaundryMachineProgram;
    let expectedResult: ILaundryMachineProgram | ILaundryMachineProgram[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(LaundryMachineProgramService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new LaundryMachineProgram(0, 'AAAAAAA', 0, 0, 0, false, false, false, false);
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
            id: 0
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
            name: 'BBBBBB',
            time: 1,
            temperature: 1,
            spin: 1,
            preWash: true,
            protect: true,
            shortCycle: true,
            wrinkle: true
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of LaundryMachineProgram', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            time: 1,
            temperature: 1,
            spin: 1,
            preWash: true,
            protect: true,
            shortCycle: true,
            wrinkle: true
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
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
