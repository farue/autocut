import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITransactionBook, TransactionBook } from '../transaction-book.model';
import { TransactionBookService } from '../service/transaction-book.service';

import { TransactionBookRoutingResolveService } from './transaction-book-routing-resolve.service';

describe('TransactionBook routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TransactionBookRoutingResolveService;
  let service: TransactionBookService;
  let resultTransactionBook: ITransactionBook | undefined;

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
    routingResolveService = TestBed.inject(TransactionBookRoutingResolveService);
    service = TestBed.inject(TransactionBookService);
    resultTransactionBook = undefined;
  });

  describe('resolve', () => {
    it('should return ITransactionBook returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTransactionBook = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTransactionBook).toEqual({ id: 123 });
    });

    it('should return new ITransactionBook if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTransactionBook = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTransactionBook).toEqual(new TransactionBook());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TransactionBook })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTransactionBook = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTransactionBook).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
