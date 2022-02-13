import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { BroadcastMessageText, IBroadcastMessageText } from '../broadcast-message-text.model';
import { BroadcastMessageTextService } from '../service/broadcast-message-text.service';

import { BroadcastMessageTextRoutingResolveService } from './broadcast-message-text-routing-resolve.service';

describe('BroadcastMessageText routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: BroadcastMessageTextRoutingResolveService;
  let service: BroadcastMessageTextService;
  let resultBroadcastMessageText: IBroadcastMessageText | undefined;

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
    routingResolveService = TestBed.inject(BroadcastMessageTextRoutingResolveService);
    service = TestBed.inject(BroadcastMessageTextService);
    resultBroadcastMessageText = undefined;
  });

  describe('resolve', () => {
    it('should return IBroadcastMessageText returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBroadcastMessageText = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBroadcastMessageText).toEqual({ id: 123 });
    });

    it('should return new IBroadcastMessageText if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBroadcastMessageText = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultBroadcastMessageText).toEqual(new BroadcastMessageText());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as BroadcastMessageText })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBroadcastMessageText = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBroadcastMessageText).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
