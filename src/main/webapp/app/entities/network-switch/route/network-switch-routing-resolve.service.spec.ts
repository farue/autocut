jest.mock('@angular/router');

import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteSnapshot, Router} from '@angular/router';
import {of} from 'rxjs';

import {INetworkSwitch, NetworkSwitch} from '../network-switch.model';
import {NetworkSwitchService} from '../service/network-switch.service';

import {NetworkSwitchRoutingResolveService} from './network-switch-routing-resolve.service';

describe('NetworkSwitch routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: NetworkSwitchRoutingResolveService;
  let service: NetworkSwitchService;
  let resultNetworkSwitch: INetworkSwitch | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(NetworkSwitchRoutingResolveService);
    service = TestBed.inject(NetworkSwitchService);
    resultNetworkSwitch = undefined;
  });

  describe('resolve', () => {
    it('should return INetworkSwitch returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNetworkSwitch = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNetworkSwitch).toEqual({ id: 123 });
    });

    it('should return new INetworkSwitch if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNetworkSwitch = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultNetworkSwitch).toEqual(new NetworkSwitch());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as NetworkSwitch })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultNetworkSwitch = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultNetworkSwitch).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
