import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRegistrationItem } from 'app/shared/model/registration-item.model';

@Component({
  selector: 'jhi-registration-item-detail',
  templateUrl: './registration-item-detail.component.html',
})
export class RegistrationItemDetailComponent implements OnInit {
  registrationItem: IRegistrationItem | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ registrationItem }) => (this.registrationItem = registrationItem));
  }

  previousState(): void {
    window.history.back();
  }
}
