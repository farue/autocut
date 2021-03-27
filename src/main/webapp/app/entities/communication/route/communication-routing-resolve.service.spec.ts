jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ICommunication, Communication } from '../communication.model';
import { CommunicationService } from '../service/communication.service';

import { CommunicationRoutingResolveService } from './communication-routing-resolve.service';

describe('Service Tests', () => {
  describe('Communication routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: CommunicationRoutingResolveService;
    let service: CommunicationService;
    let resultCommunication: ICommunication | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(CommunicationRoutingResolveService);
      service = TestBed.inject(CommunicationService);
      resultCommunication = undefined;
    });

    describe('resolve', () => {
      it('should return ICommunication returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCommunication = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCommunication).toEqual({ id: 123 });
      });

      it('should return new ICommunication if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCommunication = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultCommunication).toEqual(new Communication());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultCommunication = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultCommunication).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
