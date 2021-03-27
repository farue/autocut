import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILaundryMachine } from '../laundry-machine.model';

@Component({
  selector: 'jhi-laundry-machine-detail',
  templateUrl: './laundry-machine-detail.component.html',
})
export class LaundryMachineDetailComponent implements OnInit {
  laundryMachine: ILaundryMachine | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryMachine }) => {
      this.laundryMachine = laundryMachine;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
