import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IApartment } from 'app/shared/model/apartment.model';

@Component({
  selector: 'jhi-apartment-detail',
  templateUrl: './apartment-detail.component.html'
})
export class ApartmentDetailComponent implements OnInit {
  apartment: IApartment;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ apartment }) => {
      this.apartment = apartment;
    });
  }

  previousState() {
    window.history.back();
  }
}
