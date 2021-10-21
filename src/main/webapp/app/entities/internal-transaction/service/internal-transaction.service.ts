import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import * as dayjs from 'dayjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getInternalTransactionIdentifier, IInternalTransaction} from '../internal-transaction.model';

export type EntityResponseType = HttpResponse<IInternalTransaction>;
export type EntityArrayResponseType = HttpResponse<IInternalTransaction[]>;

@Injectable({ providedIn: 'root' })
export class InternalTransactionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/internal-transactions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(internalTransaction: IInternalTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(internalTransaction);
    return this.http
      .post<IInternalTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(internalTransaction: IInternalTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(internalTransaction);
    return this.http
      .put<IInternalTransaction>(`${this.resourceUrl}/${getInternalTransactionIdentifier(internalTransaction) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(internalTransaction: IInternalTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(internalTransaction);
    return this.http
      .patch<IInternalTransaction>(`${this.resourceUrl}/${getInternalTransactionIdentifier(internalTransaction) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IInternalTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IInternalTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addInternalTransactionToCollectionIfMissing(
    internalTransactionCollection: IInternalTransaction[],
    ...internalTransactionsToCheck: (IInternalTransaction | null | undefined)[]
  ): IInternalTransaction[] {
    const internalTransactions: IInternalTransaction[] = internalTransactionsToCheck.filter(isPresent);
    if (internalTransactions.length > 0) {
      const internalTransactionCollectionIdentifiers = internalTransactionCollection.map(
        internalTransactionItem => getInternalTransactionIdentifier(internalTransactionItem)!
      );
      const internalTransactionsToAdd = internalTransactions.filter(internalTransactionItem => {
        const internalTransactionIdentifier = getInternalTransactionIdentifier(internalTransactionItem);
        if (internalTransactionIdentifier == null || internalTransactionCollectionIdentifiers.includes(internalTransactionIdentifier)) {
          return false;
        }
        internalTransactionCollectionIdentifiers.push(internalTransactionIdentifier);
        return true;
      });
      return [...internalTransactionsToAdd, ...internalTransactionCollection];
    }
    return internalTransactionCollection;
  }

  protected convertDateFromClient(internalTransaction: IInternalTransaction): IInternalTransaction {
    return Object.assign({}, internalTransaction, {
      bookingDate: internalTransaction.bookingDate?.isValid() ? internalTransaction.bookingDate.toJSON() : undefined,
      valueDate: internalTransaction.valueDate?.isValid() ? internalTransaction.valueDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.bookingDate = res.body.bookingDate ? dayjs(res.body.bookingDate) : undefined;
      res.body.valueDate = res.body.valueDate ? dayjs(res.body.valueDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((internalTransaction: IInternalTransaction) => {
        internalTransaction.bookingDate = internalTransaction.bookingDate ? dayjs(internalTransaction.bookingDate) : undefined;
        internalTransaction.valueDate = internalTransaction.valueDate ? dayjs(internalTransaction.valueDate) : undefined;
      });
    }
    return res;
  }
}
