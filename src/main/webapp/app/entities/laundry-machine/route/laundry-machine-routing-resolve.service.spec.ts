jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ILaundryMachine, LaundryMachine } from '../laundry-machine.model';
import { LaundryMachineService } from '../service/laundry-machine.service';

import { LaundryMachineRoutingResolveService } from './laundry-machine-routing-resolve.service';

describe('LaundryMachine routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LaundryMachineRoutingResolveService;
  let service: LaundryMachineService;
  let resultLaundryMachine: ILaundryMachine | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(LaundryMachineRoutingResolveService);
    service = TestBed.inject(LaundryMachineService);
    resultLaundryMachine = undefined;
  });

  describe('resolve', () => {
    it('should return ILaundryMachine returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLaundryMachine = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLaundryMachine).toEqual({ id: 123 });
    });

    it('should return new ILaundryMachine if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLaundryMachine = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLaundryMachine).toEqual(new LaundryMachine());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as LaundryMachine })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLaundryMachine = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLaundryMachine).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
