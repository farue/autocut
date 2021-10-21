jest.mock('@angular/router');

import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteSnapshot, Router} from '@angular/router';
import {of} from 'rxjs';

import {BroadcastMessage, IBroadcastMessage} from '../broadcast-message.model';
import {BroadcastMessageService} from '../service/broadcast-message.service';

import {BroadcastMessageRoutingResolveService} from './broadcast-message-routing-resolve.service';

describe('BroadcastMessage routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: BroadcastMessageRoutingResolveService;
  let service: BroadcastMessageService;
  let resultBroadcastMessage: IBroadcastMessage | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(BroadcastMessageRoutingResolveService);
    service = TestBed.inject(BroadcastMessageService);
    resultBroadcastMessage = undefined;
  });

  describe('resolve', () => {
    it('should return IBroadcastMessage returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBroadcastMessage = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBroadcastMessage).toEqual({ id: 123 });
    });

    it('should return new IBroadcastMessage if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBroadcastMessage = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultBroadcastMessage).toEqual(new BroadcastMessage());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as BroadcastMessage })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBroadcastMessage = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBroadcastMessage).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
