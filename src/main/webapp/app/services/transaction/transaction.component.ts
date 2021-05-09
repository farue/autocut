import { Component, OnInit } from '@angular/core';
import { PAGE_SIZE_OPTIONS } from 'app/config/pagination.constants';
import { TransactionService } from './transaction.service';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ITransactionOverview } from './transaction-overview.model';
import { TranslateService } from '@ngx-translate/core';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'jhi-service-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss'],
})
export class TransactionComponent implements OnInit {
  result?: ITransactionOverview | null;

  isLoading = false;
  error = false;
  totalItems = 0;
  itemsPerPage = 10;
  pageSizeOptions = PAGE_SIZE_OPTIONS;
  page?: number;

  purpose$: Observable<string> = this.transactionService.loadPurpose();

  constructor(
    private transactionService: TransactionService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    public translateService: TranslateService
  ) {}

  ngOnInit(): void {
    this.handleNavigation();
  }

  onPageEvent(ev: PageEvent): void {
    this.loadPage(ev.pageIndex, ev.pageSize);
  }

  loadPage(page: number, size: number, dontNavigate?: boolean): void {
    this.isLoading = true;
    this.error = false;

    this.transactionService
      .query({
        page,
        size,
        sort: ['valueDate,desc', 'id,desc'],
      })
      .subscribe(
        (res: HttpResponse<ITransactionOverview>) => {
          this.isLoading = false;
          this.onSuccess(res.body, res.headers, page, size, !dontNavigate);
        },
        () => {
          this.isLoading = false;
          this.onError();
        }
      );
  }

  protected handleNavigation(): void {
    this.activatedRoute.queryParamMap.subscribe(params => {
      const page = params.get('page');
      const size = params.get('size');
      const pageNumber = page !== null ? Number(page) : 0;
      const sizeNumber = size !== null ? Number(size) : this.itemsPerPage;
      if (pageNumber !== this.page || sizeNumber !== this.itemsPerPage) {
        this.loadPage(pageNumber, sizeNumber, true);
      }
    });
  }

  protected onSuccess(data: ITransactionOverview | null, headers: HttpHeaders, page: number, size: number, navigate: boolean): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.itemsPerPage = size;
    if (navigate) {
      this.router.navigate(['/services', 'transactions'], {
        queryParams: {
          page: this.page,
          size: this.itemsPerPage,
        },
      });
    }
    this.result = data;
  }

  protected onError(): void {
    this.error = true;
  }
}
