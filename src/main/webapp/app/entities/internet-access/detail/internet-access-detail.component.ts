import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInternetAccess } from '../internet-access.model';

@Component({
  selector: 'jhi-internet-access-detail',
  templateUrl: './internet-access-detail.component.html',
})
export class InternetAccessDetailComponent implements OnInit {
  internetAccess: IInternetAccess | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ internetAccess }) => {
      this.internetAccess = internetAccess;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
