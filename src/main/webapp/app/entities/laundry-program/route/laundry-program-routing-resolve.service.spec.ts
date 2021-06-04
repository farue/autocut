jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ILaundryProgram, LaundryProgram } from '../laundry-program.model';
import { LaundryProgramService } from '../service/laundry-program.service';

import { LaundryProgramRoutingResolveService } from './laundry-program-routing-resolve.service';

describe('Service Tests', () => {
  describe('LaundryProgram routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: LaundryProgramRoutingResolveService;
    let service: LaundryProgramService;
    let resultLaundryProgram: ILaundryProgram | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(LaundryProgramRoutingResolveService);
      service = TestBed.inject(LaundryProgramService);
      resultLaundryProgram = undefined;
    });

    describe('resolve', () => {
      it('should return ILaundryProgram returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultLaundryProgram = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultLaundryProgram).toEqual({ id: 123 });
      });

      it('should return new ILaundryProgram if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultLaundryProgram = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultLaundryProgram).toEqual(new LaundryProgram());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultLaundryProgram = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultLaundryProgram).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
