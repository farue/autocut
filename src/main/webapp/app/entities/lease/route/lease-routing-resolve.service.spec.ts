jest.mock('@angular/router');

import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteSnapshot, Router} from '@angular/router';
import {of} from 'rxjs';

import {ILease, Lease} from '../lease.model';
import {LeaseService} from '../service/lease.service';

import {LeaseRoutingResolveService} from './lease-routing-resolve.service';

describe('Lease routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LeaseRoutingResolveService;
  let service: LeaseService;
  let resultLease: ILease | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(LeaseRoutingResolveService);
    service = TestBed.inject(LeaseService);
    resultLease = undefined;
  });

  describe('resolve', () => {
    it('should return ILease returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLease = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLease).toEqual({ id: 123 });
    });

    it('should return new ILease if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLease = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLease).toEqual(new Lease());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Lease })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLease = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLease).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
