import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {isPresent} from 'app/core/util/operators';
import {ApplicationConfigService} from 'app/core/config/application-config.service';
import {createRequestOption} from 'app/core/request/request-util';
import {getTransactionBookIdentifier, ITransactionBook} from '../transaction-book.model';

export type EntityResponseType = HttpResponse<ITransactionBook>;
export type EntityArrayResponseType = HttpResponse<ITransactionBook[]>;

@Injectable({ providedIn: 'root' })
export class TransactionBookService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/transaction-books');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(transactionBook: ITransactionBook): Observable<EntityResponseType> {
    return this.http.post<ITransactionBook>(this.resourceUrl, transactionBook, { observe: 'response' });
  }

  update(transactionBook: ITransactionBook): Observable<EntityResponseType> {
    return this.http.put<ITransactionBook>(
      `${this.resourceUrl}/${getTransactionBookIdentifier(transactionBook) as number}`,
      transactionBook,
      { observe: 'response' }
    );
  }

  partialUpdate(transactionBook: ITransactionBook): Observable<EntityResponseType> {
    return this.http.patch<ITransactionBook>(
      `${this.resourceUrl}/${getTransactionBookIdentifier(transactionBook) as number}`,
      transactionBook,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITransactionBook>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransactionBook[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTransactionBookToCollectionIfMissing(
    transactionBookCollection: ITransactionBook[],
    ...transactionBooksToCheck: (ITransactionBook | null | undefined)[]
  ): ITransactionBook[] {
    const transactionBooks: ITransactionBook[] = transactionBooksToCheck.filter(isPresent);
    if (transactionBooks.length > 0) {
      const transactionBookCollectionIdentifiers = transactionBookCollection.map(
        transactionBookItem => getTransactionBookIdentifier(transactionBookItem)!
      );
      const transactionBooksToAdd = transactionBooks.filter(transactionBookItem => {
        const transactionBookIdentifier = getTransactionBookIdentifier(transactionBookItem);
        if (transactionBookIdentifier == null || transactionBookCollectionIdentifiers.includes(transactionBookIdentifier)) {
          return false;
        }
        transactionBookCollectionIdentifiers.push(transactionBookIdentifier);
        return true;
      });
      return [...transactionBooksToAdd, ...transactionBookCollection];
    }
    return transactionBookCollection;
  }
}
