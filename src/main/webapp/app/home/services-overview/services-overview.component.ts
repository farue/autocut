import { Component } from '@angular/core';
import { WashingService } from 'app/services/washing/washing.service';
import { isEqual, last } from 'lodash-es';
import { forkJoin, Observable, of, timer } from 'rxjs';
import { catchError, distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
import { Machine } from 'app/entities/washing/washing.model';
import { LoggedInUserService } from 'app/shared/service/logged-in-user.service';
import { MediaService } from 'app/shared/service/media.service';
import * as dayjs from 'dayjs';
import { InternalTransaction } from 'app/entities/internal-transaction/internal-transaction.model';
import { HttpResponse } from '@angular/common/http';
import { isPresent } from 'app/core/util/operators';
import { TransactionBook } from 'app/entities/transaction/transaction-book.model';

interface TransactionBookWithTransaction {
  transactionBook: TransactionBook;
  transaction: InternalTransaction | null;
}

@Component({
  selector: 'jhi-services-overview',
  templateUrl: './services-overview.component.html',
  styleUrls: ['./services-overview.component.scss'],
})
export class ServicesOverviewComponent {
  machines: Machine[] = [];
  machines$: Observable<Machine[]> = timer(0, 60000).pipe(
    switchMap(v =>
      // create inner observable to continue outer observable on errors
      of(v).pipe(
        switchMap(() => this.washingService.getAllMachines()),
        catchError(err => {
          console.error(err);
          return of(this.machines);
        })
      )
    ),
    distinctUntilChanged(isEqual)
  );

  networkStatus$ = this.loggedInUserService.networkStatus();

  balances$: Observable<TransactionBookWithTransaction[]> = this.loggedInUserService.transactionBooks().pipe(
    switchMap(transactionBooks =>
      forkJoin(
        transactionBooks.map(transactionBook =>
          this.loggedInUserService
            .transactions(transactionBook.id, dayjs(), undefined, {
              page: 0,
              size: Number.MAX_SAFE_INTEGER,
              sort: ['valueDate,asc', 'id,asc'],
            })
            .pipe(
              map((res: HttpResponse<InternalTransaction[]>) => res.body),
              filter(isPresent),
              map((transactions: InternalTransaction[]) => this.findFirstRelevantTransaction(transactions)),
              map((transaction: InternalTransaction | null) => ({ transactionBook, transaction }))
            )
        )
      )
    )
  );

  constructor(
    private washingService: WashingService,
    private loggedInUserService: LoggedInUserService,
    public mediaService: MediaService
  ) {}

  private findFirstRelevantTransaction(transactions: InternalTransaction[]): InternalTransaction | null {
    for (const transaction of transactions) {
      if (transaction.balanceAfter! < 0) {
        return transaction;
      }
    }
    return last(transactions) ?? null;
  }
}
