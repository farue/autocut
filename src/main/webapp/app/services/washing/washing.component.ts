import { Component, OnInit } from '@angular/core';
import { WashingService } from 'app/services/washing/washing.service';
import { LaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { HttpErrorResponse } from '@angular/common/http';
import { INSUFFICIENT_FUNDS_TYPE, LAUNDRY_MACHINE_UNAVAILABLE_TYPE } from 'app/shared/constants/error.constants';

@Component({
  selector: 'jhi-application',
  templateUrl: './washing.component.html',
  styleUrls: ['washing.component.scss']
})
export class WashingComponent implements OnInit {
  message: string;

  machines?: LaundryMachine[];
  selectedMachine?: LaundryMachine;
  selectedProgram?: LaundryMachineProgram;

  initError = false;
  error = false;
  success = false;
  errorInsufficientFunds = false;
  errorMachineUnavailable = false;

  constructor(private washingService: WashingService) {
    this.message = 'Washing message';
  }

  ngOnInit(): void {
    this.washingService.getAllLaundryMachines().subscribe(
      (machines: LaundryMachine[]) => {
        this.initError = false;
        this.machines = machines;
      },
      () => (this.initError = true)
    );
  }

  unlockAction(): void {
    if (this.selectedMachine && this.selectedProgram) {
      this.unlock(this.selectedMachine, this.selectedProgram);
    }
  }

  unlock(laundryMachine: LaundryMachine, laundryMachineProgram: LaundryMachineProgram): void {
    this.error = false;
    this.errorInsufficientFunds = false;
    this.errorMachineUnavailable = false;

    this.washingService.unlock(laundryMachine, laundryMachineProgram).subscribe(
      () => (this.success = true),
      response => this.processError(response)
    );
  }

  invalid(): boolean {
    return !this.selectedMachine || !this.selectedProgram;
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === INSUFFICIENT_FUNDS_TYPE) {
      this.errorInsufficientFunds = true;
    } else if (response.status === 400 && response.error.type === LAUNDRY_MACHINE_UNAVAILABLE_TYPE) {
      this.errorMachineUnavailable = true;
    } else {
      this.error = true;
    }
  }
}
