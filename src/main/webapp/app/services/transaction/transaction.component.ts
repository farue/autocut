import { Component, OnInit } from '@angular/core';
import { ITransaction } from '../../shared/model/transaction.model';
import { ITEMS_PER_PAGE } from '../../shared/constants/pagination.constants';
import { TransactionService } from './transaction.service';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { ITransactionOverview } from './transaction-overview.model';

@Component({
  selector: 'jhi-service-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['transaction.component.scss'],
})
export class TransactionComponent implements OnInit {
  balance = 0;
  deposit = 0;
  transactions: ITransaction[] | null | undefined = null;

  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;

  constructor(private transactionService: TransactionService, private activatedRoute: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    this.handleNavigation();
    this.loadTransactionOverview();
  }

  loadTransactionOverview(): void {
    this.transactionService
      .query({
        page: this.page - 1,
        size: this.itemsPerPage,
        sort: [],
      })
      .subscribe((res: HttpResponse<ITransactionOverview>) => this.onSuccess(res.body, res.headers));
  }

  transition(): void {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute.parent,
      queryParams: {
        page: this.page,
      },
    });
  }

  private handleNavigation(): void {
    combineLatest(this.activatedRoute.data, this.activatedRoute.queryParamMap, (data: Data, params: ParamMap) => {
      const page = params.get('page');
      this.page = page !== null ? +page : 1;
    }).subscribe();
  }

  private onSuccess(transactionOverview: ITransactionOverview | null, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.balance = transactionOverview?.balanceNow || 0;
    this.deposit = transactionOverview?.deposit || 0;
    this.transactions = transactionOverview?.transactions;
  }
}
