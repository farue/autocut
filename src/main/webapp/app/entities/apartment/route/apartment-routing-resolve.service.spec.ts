jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IApartment, Apartment } from '../apartment.model';
import { ApartmentService } from '../service/apartment.service';

import { ApartmentRoutingResolveService } from './apartment-routing-resolve.service';

describe('Service Tests', () => {
  describe('Apartment routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: ApartmentRoutingResolveService;
    let service: ApartmentService;
    let resultApartment: IApartment | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(ApartmentRoutingResolveService);
      service = TestBed.inject(ApartmentService);
      resultApartment = undefined;
    });

    describe('resolve', () => {
      it('should return IApartment returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultApartment = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultApartment).toEqual({ id: 123 });
      });

      it('should return new IApartment if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultApartment = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultApartment).toEqual(new Apartment());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultApartment = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultApartment).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
