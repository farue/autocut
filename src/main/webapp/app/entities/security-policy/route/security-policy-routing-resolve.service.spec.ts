import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ISecurityPolicy, SecurityPolicy } from '../security-policy.model';
import { SecurityPolicyService } from '../service/security-policy.service';

import { SecurityPolicyRoutingResolveService } from './security-policy-routing-resolve.service';

describe('SecurityPolicy routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: SecurityPolicyRoutingResolveService;
  let service: SecurityPolicyService;
  let resultSecurityPolicy: ISecurityPolicy | undefined;

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
    routingResolveService = TestBed.inject(SecurityPolicyRoutingResolveService);
    service = TestBed.inject(SecurityPolicyService);
    resultSecurityPolicy = undefined;
  });

  describe('resolve', () => {
    it('should return ISecurityPolicy returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSecurityPolicy = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSecurityPolicy).toEqual({ id: 123 });
    });

    it('should return new ISecurityPolicy if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSecurityPolicy = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSecurityPolicy).toEqual(new SecurityPolicy());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as SecurityPolicy })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSecurityPolicy = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSecurityPolicy).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
