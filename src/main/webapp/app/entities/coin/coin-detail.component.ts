import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICoin } from 'app/shared/model/coin.model';

@Component({
  selector: 'jhi-coin-detail',
  templateUrl: './coin-detail.component.html'
})
export class CoinDetailComponent implements OnInit {
  coin: ICoin | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coin }) => {
      this.coin = coin;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
