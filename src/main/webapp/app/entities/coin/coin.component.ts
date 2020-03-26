import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICoin } from 'app/shared/model/coin.model';
import { CoinService } from './coin.service';
import { CoinDeleteDialogComponent } from './coin-delete-dialog.component';

@Component({
  selector: 'jhi-coin',
  templateUrl: './coin.component.html'
})
export class CoinComponent implements OnInit, OnDestroy {
  coins?: ICoin[];
  eventSubscriber?: Subscription;

  constructor(protected coinService: CoinService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.coinService.query().subscribe((res: HttpResponse<ICoin[]>) => {
      this.coins = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCoins();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICoin): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCoins(): void {
    this.eventSubscriber = this.eventManager.subscribe('coinListModification', () => this.loadAll());
  }

  delete(coin: ICoin): void {
    const modalRef = this.modalService.open(CoinDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.coin = coin;
  }
}
