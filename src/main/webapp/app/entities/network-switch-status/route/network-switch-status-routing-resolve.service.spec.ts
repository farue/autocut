jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { INetworkSwitchStatus, NetworkSwitchStatus } from '../network-switch-status.model';
import { NetworkSwitchStatusService } from '../service/network-switch-status.service';

import { NetworkSwitchStatusRoutingResolveService } from './network-switch-status-routing-resolve.service';

describe('NetworkSwitchStatus routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: NetworkSwitchStatusRoutingResolveService;
  let service: NetworkSwitchStatusService;
  let resultNetworkSwitchStatus: INetworkSwitchStatus | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(NetworkSwitchStatusRoutingResolveService);
    service = TestBed.inject(NetworkSwitchStatusService);
    resultNetworkSwitchStatus = undefined;
  });

  describe('resolve', () => {
    it('should return INetworkSwitchStatus returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNetworkSwitchStatus = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNetworkSwitchStatus).toEqual({ id: 123 });
    });

    it('should return new INetworkSwitchStatus if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNetworkSwitchStatus = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultNetworkSwitchStatus).toEqual(new NetworkSwitchStatus());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as NetworkSwitchStatus })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNetworkSwitchStatus = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNetworkSwitchStatus).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
