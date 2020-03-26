import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IWashHistory } from 'app/shared/model/wash-history.model';

@Component({
  selector: 'jhi-wash-history-detail',
  templateUrl: './wash-history-detail.component.html'
})
export class WashHistoryDetailComponent implements OnInit {
  washHistory: IWashHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ washHistory }) => {
      this.washHistory = washHistory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
