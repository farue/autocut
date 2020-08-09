import { Component, OnInit } from '@angular/core';
import { WashingService } from 'app/services/washing/washing.service';
import { LaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { HttpErrorResponse } from '@angular/common/http';
import { INSUFFICIENT_FUNDS_TYPE, LAUNDRY_MACHINE_UNAVAILABLE_TYPE } from 'app/shared/constants/error.constants';
import { LaundryMachineType } from 'app/shared/model/enumerations/laundry-machine-type.model';

@Component({
  selector: 'jhi-application',
  templateUrl: './washing.component.html',
  styleUrls: ['washing.component.scss'],
})
export class WashingComponent implements OnInit {
  message: string;

  machines: LaundryMachine[] = [];
  programs: string[] = [];
  subprograms: string[] = [];
  spins: number[] = [];

  selectedMachine?: LaundryMachine;
  selectedProgram?: string;
  selectedSubprogram?: string;
  selectedSpin?: number;
  selectedPreWash?: boolean;
  selectedProtect?: boolean;

  initError = false;
  error = false;
  success = false;
  errorInsufficientFunds = false;
  errorMachineUnavailable = false;

  constructor(private washingService: WashingService) {
    this.message = 'Washing message';
  }

  private static notEmpty<TValue>(value: TValue | null | undefined): value is TValue {
    return value !== null && value !== undefined;
  }

  private static onlyUnique<TValue>(value: TValue, index: number, self: TValue[]): boolean {
    return self.indexOf(value) === index;
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
    const selectedProgram = this.getSelectedProgram();
    if (this.selectedMachine != null && selectedProgram != null) {
      this.unlock(this.selectedMachine, selectedProgram);
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
    return this.getSelectedProgram() == null;
  }

  private getSelectedProgram(): LaundryMachineProgram | null {
    const programs = this.filterPrograms(
      this.selectedProgram,
      this.selectedSubprogram,
      this.selectedSpin,
      this.selectedPreWash,
      this.selectedProtect
    );
    if (programs.length === 1) {
      return programs[0];
    } else {
      return null;
    }
  }

  private filterPrograms(
    program?: string,
    subprogram?: string,
    spin?: number,
    preWash?: boolean,
    protect?: boolean
  ): LaundryMachineProgram[] {
    if (this.selectedMachine == null) {
      return [];
    }
    return this.selectedMachine
      .programs!.filter(p => (p.name == null && program == null) || p.name === program)
      .filter(p => (p.subprogram == null && subprogram == null) || p.subprogram === subprogram)
      .filter(p => (p.spin == null && spin == null) || p.spin === spin)
      .filter(p => (p.preWash == null && !preWash) || p.preWash === preWash)
      .filter(p => (p.protect == null && !protect) || p.protect === protect);
  }

  isShowProgramSelect(): boolean {
    return this.selectedMachine != null;
  }

  isShowSubprogramSelect(): boolean {
    return this.isShowProgramSelect() && this.selectedProgram != null && this.getSubprogramsForProgram(this.selectedProgram).length > 0;
  }

  isShowSpinSelect(): boolean {
    if (this.selectedMachine != null && this.selectedMachine.type === LaundryMachineType.WASHING_MACHINE && this.selectedProgram != null) {
      return !this.isShowSubprogramSelect() || this.selectedSubprogram != null;
    }
    return false;
  }

  isShowPreWashCheckbox(): boolean {
    if (this.selectedMachine != null && this.selectedMachine.type === LaundryMachineType.WASHING_MACHINE && this.selectedProgram != null) {
      return !this.isShowSubprogramSelect() || this.selectedSubprogram != null;
    }
    return false;
  }

  isShowProtectCheckbox(): boolean {
    if (this.selectedMachine != null && this.selectedMachine.type === LaundryMachineType.DRYER && this.selectedProgram != null) {
      if (!this.isShowSubprogramSelect() || this.selectedSubprogram != null) {
        return (
          this.getProgramsFilteredByProgramNameAndSubprogram(this.selectedProgram, this.selectedSubprogram).filter(p =>
            WashingComponent.notEmpty(p.protect)
          ).length > 0
        );
      }
    }
    return false;
  }

  getPrograms(): string[] {
    if (this.selectedMachine == null) {
      return [];
    }
    return this.selectedMachine.programs!.map(p => p.name!).filter(WashingComponent.onlyUnique);
  }

  getSubprograms(): string[] {
    if (this.selectedProgram == null) {
      return [];
    }
    return this.getSubprogramsForProgram(this.selectedProgram);
  }

  getSpins(): number[] {
    if (!this.isShowSpinSelect()) {
      return [];
    }
    return this.getProgramsFilteredByProgramNameAndSubprogram(this.selectedProgram!, this.selectedSubprogram)
      .map(p => p.spin)
      .filter(WashingComponent.notEmpty)
      .filter(WashingComponent.onlyUnique);
  }

  update(): void {
    this.success = false;
    if (!this.isShowProgramSelect()) {
      this.selectedProgram = undefined;
    }
    if (!this.isShowSubprogramSelect()) {
      this.selectedSubprogram = undefined;
    }
    if (!this.isShowSpinSelect()) {
      this.selectedSpin = undefined;
    }
    if (!this.isShowPreWashCheckbox()) {
      this.selectedPreWash = undefined;
    }
    if (!this.isShowProtectCheckbox()) {
      this.selectedProtect = undefined;
    }
    if (this.isShowPreWashCheckbox() && !this.selectedPreWash) {
      this.selectedPreWash = false;
    }
    if (this.isShowProtectCheckbox() && !this.selectedProtect) {
      this.selectedProtect = false;
    }
    this.updatePrograms();
    this.updateSubprograms();
    this.updateSpins();
  }

  updatePrograms(): void {
    this.programs = this.getPrograms();
  }

  updateSubprograms(): void {
    this.subprograms = this.getSubprograms();
  }

  updateSpins(): void {
    this.spins = this.getSpins();
  }

  private getSubprogramsForProgram(program: string): string[] {
    if (this.selectedMachine == null || program == null) {
      return [];
    }
    return this.selectedMachine
      .programs!.filter(p => p.name === program)
      .map(p => p.subprogram)
      .filter(WashingComponent.notEmpty)
      .filter(WashingComponent.onlyUnique);
  }

  private getProgramsFilteredByProgramNameAndSubprogram(program: string, subprogram?: string): LaundryMachineProgram[] {
    if (this.selectedMachine == null) {
      return [];
    }
    return this.selectedMachine.programs!.filter(p => {
      if (subprogram != null) {
        return p.name === program && p.subprogram === subprogram;
      } else {
        return p.name === program;
      }
    });
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
