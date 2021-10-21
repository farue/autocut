jest.mock('@angular/router');

import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteSnapshot, Router} from '@angular/router';
import {of} from 'rxjs';

import {GlobalSetting, IGlobalSetting} from '../global-setting.model';
import {GlobalSettingService} from '../service/global-setting.service';

import {GlobalSettingRoutingResolveService} from './global-setting-routing-resolve.service';

describe('GlobalSetting routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: GlobalSettingRoutingResolveService;
  let service: GlobalSettingService;
  let resultGlobalSetting: IGlobalSetting | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(GlobalSettingRoutingResolveService);
    service = TestBed.inject(GlobalSettingService);
    resultGlobalSetting = undefined;
  });

  describe('resolve', () => {
    it('should return IGlobalSetting returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGlobalSetting = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGlobalSetting).toEqual({ id: 123 });
    });

    it('should return new IGlobalSetting if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGlobalSetting = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultGlobalSetting).toEqual(new GlobalSetting());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as GlobalSetting })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultGlobalSetting = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultGlobalSetting).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
