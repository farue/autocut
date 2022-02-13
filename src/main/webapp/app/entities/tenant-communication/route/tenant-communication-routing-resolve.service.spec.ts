import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITenantCommunication, TenantCommunication } from '../tenant-communication.model';
import { TenantCommunicationService } from '../service/tenant-communication.service';

import { TenantCommunicationRoutingResolveService } from './tenant-communication-routing-resolve.service';

describe('TenantCommunication routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TenantCommunicationRoutingResolveService;
  let service: TenantCommunicationService;
  let resultTenantCommunication: ITenantCommunication | undefined;

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
    routingResolveService = TestBed.inject(TenantCommunicationRoutingResolveService);
    service = TestBed.inject(TenantCommunicationService);
    resultTenantCommunication = undefined;
  });

  describe('resolve', () => {
    it('should return ITenantCommunication returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTenantCommunication = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTenantCommunication).toEqual({ id: 123 });
    });

    it('should return new ITenantCommunication if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTenantCommunication = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTenantCommunication).toEqual(new TenantCommunication());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TenantCommunication })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTenantCommunication = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTenantCommunication).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
