import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IWashHistory, WashHistory } from '../wash-history.model';
import { WashHistoryService } from '../service/wash-history.service';

import { WashHistoryRoutingResolveService } from './wash-history-routing-resolve.service';

describe('WashHistory routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: WashHistoryRoutingResolveService;
  let service: WashHistoryService;
  let resultWashHistory: IWashHistory | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(WashHistoryRoutingResolveService);
    service = TestBed.inject(WashHistoryService);
    resultWashHistory = undefined;
  });

  describe('resolve', () => {
    it('should return IWashHistory returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWashHistory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultWashHistory).toEqual({ id: 123 });
    });

    it('should return new IWashHistory if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWashHistory = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultWashHistory).toEqual(new WashHistory());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as WashHistory })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultWashHistory = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultWashHistory).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
