import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITotp } from 'app/shared/model/totp.model';

@Component({
  selector: 'jhi-totp-detail',
  templateUrl: './totp-detail.component.html'
})
export class TotpDetailComponent implements OnInit {
  totp: ITotp | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ totp }) => {
      this.totp = totp;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
