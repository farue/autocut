import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IInternetAccess, InternetAccess } from '../internet-access.model';
import { InternetAccessService } from '../service/internet-access.service';

import { InternetAccessRoutingResolveService } from './internet-access-routing-resolve.service';

describe('InternetAccess routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: InternetAccessRoutingResolveService;
  let service: InternetAccessService;
  let resultInternetAccess: IInternetAccess | undefined;

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
    routingResolveService = TestBed.inject(InternetAccessRoutingResolveService);
    service = TestBed.inject(InternetAccessService);
    resultInternetAccess = undefined;
  });

  describe('resolve', () => {
    it('should return IInternetAccess returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultInternetAccess = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultInternetAccess).toEqual({ id: 123 });
    });

    it('should return new IInternetAccess if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultInternetAccess = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultInternetAccess).toEqual(new InternetAccess());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as InternetAccess })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultInternetAccess = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultInternetAccess).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
