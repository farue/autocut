import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IInternetAccess } from 'app/shared/model/internet-access.model';

@Component({
  selector: 'jhi-internet-access-detail',
  templateUrl: './internet-access-detail.component.html'
})
export class InternetAccessDetailComponent implements OnInit {
  internetAccess: IInternetAccess;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ internetAccess }) => {
      this.internetAccess = internetAccess;
    });
  }

  previousState() {
    window.history.back();
  }
}
