import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILaundryProgram } from '../laundry-program.model';

@Component({
  selector: 'jhi-laundry-program-detail',
  templateUrl: './laundry-program-detail.component.html',
})
export class LaundryProgramDetailComponent implements OnInit {
  laundryProgram: ILaundryProgram | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryProgram }) => {
      this.laundryProgram = laundryProgram;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
