import { getTestBed, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { NetworkSwitchStatusService } from 'app/entities/network-switch-status/network-switch-status.service';
import { INetworkSwitchStatus, NetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';

describe('Service Tests', () => {
  describe('NetworkSwitchStatus Service', () => {
    let injector: TestBed;
    let service: NetworkSwitchStatusService;
    let httpMock: HttpTestingController;
    let elemDefault: INetworkSwitchStatus;
    let expectedResult: INetworkSwitchStatus | INetworkSwitchStatus[] | boolean | null;
    let currentDate: moment.Moment;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      injector = getTestBed();
      service = injector.get(NetworkSwitchStatusService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new NetworkSwitchStatus(0, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', currentDate);
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            timestamp: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a NetworkSwitchStatus', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            timestamp: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            timestamp: currentDate,
          },
          returnedFromService
        );

        service.create(new NetworkSwitchStatus()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a NetworkSwitchStatus', () => {
        const returnedFromService = Object.assign(
          {
            port: 'BBBBBB',
            name: 'BBBBBB',
            status: 'BBBBBB',
            vlan: 'BBBBBB',
            speed: 'BBBBBB',
            type: 'BBBBBB',
            timestamp: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            timestamp: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of NetworkSwitchStatus', () => {
        const returnedFromService = Object.assign(
          {
            port: 'BBBBBB',
            name: 'BBBBBB',
            status: 'BBBBBB',
            vlan: 'BBBBBB',
            speed: 'BBBBBB',
            type: 'BBBBBB',
            timestamp: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            timestamp: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a NetworkSwitchStatus', () => {
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
