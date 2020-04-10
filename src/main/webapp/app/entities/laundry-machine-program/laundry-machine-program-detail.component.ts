import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

@Component({
  selector: 'jhi-laundry-machine-program-detail',
  templateUrl: './laundry-machine-program-detail.component.html'
})
export class LaundryMachineProgramDetailComponent implements OnInit {
  laundryMachineProgram: ILaundryMachineProgram | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ laundryMachineProgram }) => (this.laundryMachineProgram = laundryMachineProgram));
  }

  previousState(): void {
    window.history.back();
  }
}
