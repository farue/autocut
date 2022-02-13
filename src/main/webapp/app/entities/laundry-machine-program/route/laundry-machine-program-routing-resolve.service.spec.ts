import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ILaundryMachineProgram, LaundryMachineProgram } from '../laundry-machine-program.model';
import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';

import { LaundryMachineProgramRoutingResolveService } from './laundry-machine-program-routing-resolve.service';

describe('LaundryMachineProgram routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: LaundryMachineProgramRoutingResolveService;
  let service: LaundryMachineProgramService;
  let resultLaundryMachineProgram: ILaundryMachineProgram | undefined;

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
    routingResolveService = TestBed.inject(LaundryMachineProgramRoutingResolveService);
    service = TestBed.inject(LaundryMachineProgramService);
    resultLaundryMachineProgram = undefined;
  });

  describe('resolve', () => {
    it('should return ILaundryMachineProgram returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLaundryMachineProgram = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLaundryMachineProgram).toEqual({ id: 123 });
    });

    it('should return new ILaundryMachineProgram if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLaundryMachineProgram = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultLaundryMachineProgram).toEqual(new LaundryMachineProgram());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as LaundryMachineProgram })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultLaundryMachineProgram = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultLaundryMachineProgram).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
