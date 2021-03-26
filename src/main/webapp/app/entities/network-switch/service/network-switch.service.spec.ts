import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { INetworkSwitch, NetworkSwitch } from '../network-switch.model';

import { NetworkSwitchService } from './network-switch.service';

describe('Service Tests', () => {
  describe('NetworkSwitch Service', () => {
    let service: NetworkSwitchService;
    let httpMock: HttpTestingController;
    let elemDefault: INetworkSwitch;
    let expectedResult: INetworkSwitch | INetworkSwitch[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(NetworkSwitchService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        interfaceName: 'AAAAAAA',
        sshHost: 'AAAAAAA',
        sshPort: 0,
        sshKey: 'AAAAAAA',
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

      it('should create a NetworkSwitch', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new NetworkSwitch()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a NetworkSwitch', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            interfaceName: 'BBBBBB',
            sshHost: 'BBBBBB',
            sshPort: 1,
            sshKey: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a NetworkSwitch', () => {
        const patchObject = Object.assign(
          {
            sshHost: 'BBBBBB',
            sshPort: 1,
            sshKey: 'BBBBBB',
          },
          new NetworkSwitch()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of NetworkSwitch', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            interfaceName: 'BBBBBB',
            sshHost: 'BBBBBB',
            sshPort: 1,
            sshKey: 'BBBBBB',
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

      it('should delete a NetworkSwitch', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addNetworkSwitchToCollectionIfMissing', () => {
        it('should add a NetworkSwitch to an empty array', () => {
          const networkSwitch: INetworkSwitch = { id: 123 };
          expectedResult = service.addNetworkSwitchToCollectionIfMissing([], networkSwitch);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(networkSwitch);
        });

        it('should not add a NetworkSwitch to an array that contains it', () => {
          const networkSwitch: INetworkSwitch = { id: 123 };
          const networkSwitchCollection: INetworkSwitch[] = [
            {
              ...networkSwitch,
            },
            { id: 456 },
          ];
          expectedResult = service.addNetworkSwitchToCollectionIfMissing(networkSwitchCollection, networkSwitch);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a NetworkSwitch to an array that doesn't contain it", () => {
          const networkSwitch: INetworkSwitch = { id: 123 };
          const networkSwitchCollection: INetworkSwitch[] = [{ id: 456 }];
          expectedResult = service.addNetworkSwitchToCollectionIfMissing(networkSwitchCollection, networkSwitch);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(networkSwitch);
        });

        it('should add only unique NetworkSwitch to an array', () => {
          const networkSwitchArray: INetworkSwitch[] = [{ id: 123 }, { id: 456 }, { id: 96912 }];
          const networkSwitchCollection: INetworkSwitch[] = [{ id: 123 }];
          expectedResult = service.addNetworkSwitchToCollectionIfMissing(networkSwitchCollection, ...networkSwitchArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const networkSwitch: INetworkSwitch = { id: 123 };
          const networkSwitch2: INetworkSwitch = { id: 456 };
          expectedResult = service.addNetworkSwitchToCollectionIfMissing([], networkSwitch, networkSwitch2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(networkSwitch);
          expect(expectedResult).toContain(networkSwitch2);
        });

        it('should accept null and undefined values', () => {
          const networkSwitch: INetworkSwitch = { id: 123 };
          expectedResult = service.addNetworkSwitchToCollectionIfMissing([], null, networkSwitch, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(networkSwitch);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
