jest.mock('@angular/router');

import {TestBed} from '@angular/core/testing';
import {HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteSnapshot, Router} from '@angular/router';
import {of} from 'rxjs';

import {IInternalTransaction, InternalTransaction} from '../internal-transaction.model';
import {InternalTransactionService} from '../service/internal-transaction.service';

import {InternalTransactionRoutingResolveService} from './internal-transaction-routing-resolve.service';

describe('InternalTransaction routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: InternalTransactionRoutingResolveService;
  let service: InternalTransactionService;
  let resultInternalTransaction: IInternalTransaction | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(InternalTransactionRoutingResolveService);
    service = TestBed.inject(InternalTransactionService);
    resultInternalTransaction = undefined;
  });

  describe('resolve', () => {
    it('should return IInternalTransaction returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultInternalTransaction = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultInternalTransaction).toEqual({ id: 123 });
    });

    it('should return new IInternalTransaction if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultInternalTransaction = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultInternalTransaction).toEqual(new InternalTransaction());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as InternalTransaction })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultInternalTransaction = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultInternalTransaction).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
