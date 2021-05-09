import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivateResponse, WashingService } from 'app/services/washing/washing.service';
import { LaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { LaundryMachineProgram } from 'app/entities/laundry-machine-program/laundry-machine-program.model';
import { HttpErrorResponse } from '@angular/common/http';
import { INSUFFICIENT_FUNDS_TYPE, LAUNDRY_MACHINE_UNAVAILABLE_TYPE } from 'app/config/error.constants';
import { LaundryMachineType } from 'app/entities/enumerations/laundry-machine-type.model';
import { FormBuilder, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import * as _ from 'lodash';
import * as dayjs from 'dayjs';
import { TranslateService } from '@ngx-translate/core';
import { bounceOnEnterAnimation } from 'angular-animations';

enum FormProperties {
  LAUNDRY_MACHINE = 'laundryMachine',
  PROGRAM = 'program',
  SUBPROGRAM = 'subprogram',
  SPIN = 'spin',
  PREWASH = 'preWash',
  PROTECT = 'protect',
}

interface WashingResponse {
  machineId?: number;
  activationTimestamp?: dayjs.Dayjs;
  endActivationTime?: dayjs.Dayjs;
  activationDurationMs?: number;
  countdownSeconds?: number;
  success?: boolean;
  genericError?: boolean;
  errorInsufficientFunds?: boolean;
  errorMachineUnavailable?: boolean;
}

@Component({
  selector: 'jhi-application',
  templateUrl: './washing.component.html',
  styleUrls: ['./washing.component.scss'],
  animations: [bounceOnEnterAnimation()],
})
export class WashingComponent implements OnInit {
  FormProperties = FormProperties;

  machines: LaundryMachine[] = [];
  programs: string[] = [];
  subprograms: string[] = [];
  spins: number[] = [];

  initError = false;

  loadingMachines: boolean;
  loadingActivate = false;

  response?: WashingResponse;

  formGroup = this.fb.group({
    [FormProperties.LAUNDRY_MACHINE]: [{ value: null, disabled: false }, Validators.required],
    [FormProperties.PROGRAM]: [{ value: null, disabled: true }, Validators.required],
    [FormProperties.SUBPROGRAM]: [{ value: null, disabled: true }, Validators.required],
    [FormProperties.SPIN]: [{ value: null, disabled: true }, Validators.required],
    [FormProperties.PREWASH]: [{ value: null, disabled: true }],
    [FormProperties.PROTECT]: [{ value: null, disabled: true }],
  });

  constructor(
    private fb: FormBuilder,
    private washingService: WashingService,
    private translateService: TranslateService,
    private cd: ChangeDetectorRef
  ) {
    this.loadingMachines = true;
    this.washingService
      .getAllLaundryMachines()
      .pipe(finalize(() => (this.loadingMachines = false)))
      .subscribe(
        (machines: LaundryMachine[]) => {
          this.initError = false;
          this.machines = machines;
        },
        () => (this.initError = true)
      );

    this.formGroup.valueChanges.subscribe(() => this.update());
  }

  private static notEmpty<TValue>(value: TValue | null | undefined): value is TValue {
    return value !== null && value !== undefined;
  }

  private static onlyUnique<TValue>(value: TValue, index: number, self: TValue[]): boolean {
    return self.indexOf(value) === index;
  }

  get selectedMachine(): LaundryMachine | null {
    return this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.value as LaundryMachine | null;
  }

  get selectedProgram(): string | null {
    return this.formGroup.get(FormProperties.PROGRAM)!.value as string | null;
  }

  get selectedSubprogram(): string | null {
    return this.formGroup.get(FormProperties.SUBPROGRAM)!.value as string | null;
  }

  get selectedSpin(): number | null {
    return this.formGroup.get(FormProperties.SPIN)!.value as number | null;
  }

  get selectedPreWash(): boolean | null {
    return this.formGroup.get(FormProperties.PREWASH)!.value as boolean | null;
  }

  get selectedProtect(): boolean | null {
    return this.formGroup.get(FormProperties.PROTECT)!.value as boolean | null;
  }

  // eslint-disable-next-line @angular-eslint/no-empty-lifecycle-method,@typescript-eslint/no-empty-function
  ngOnInit(): void {}

  unlockAction(): void {
    const selectedProgram = this.getSelectedProgram();
    if (this.selectedMachine != null) {
      this.unlock(this.selectedMachine, selectedProgram);
    }
  }

  unlock(laundryMachine: LaundryMachine, laundryMachineProgram: LaundryMachineProgram): void {
    this.loadingActivate = true;
    this.response = undefined;

    this.washingService
      .unlock(laundryMachine, laundryMachineProgram)
      .pipe(finalize(() => (this.loadingActivate = false)))
      .subscribe(
        (response: ActivateResponse) => {
          this.response = {
            ...response,
            countdownSeconds: response.endActivationTime.diff(dayjs(), 'seconds'),
            success: true,
          };
        },
        (err: HttpErrorResponse) => {
          this.response = { success: false, machineId: laundryMachine.id! };
          if (err.status === 400 && err.error.type === INSUFFICIENT_FUNDS_TYPE) {
            this.response.errorInsufficientFunds = true;
          } else if (err.status === 400 && err.error.type === LAUNDRY_MACHINE_UNAVAILABLE_TYPE) {
            this.response.errorMachineUnavailable = true;
          } else {
            this.response.genericError = true;
          }
        }
      );
  }

  onCountdownFinished(): void {
    if (this.response?.countdownSeconds) {
      this.response.countdownSeconds = undefined;
    }
  }

  getSelectedProgram(): LaundryMachineProgram {
    return this.findSingleMatchingProgram(
      this.selectedProgram,
      this.selectedSubprogram,
      this.selectedSpin,
      this.selectedPreWash,
      this.selectedProtect
    );
  }

  filterPrograms(
    machine?: LaundryMachine | null,
    program?: string | null,
    subprogram?: string | null,
    spin?: number | null,
    preWash?: boolean | null,
    protect?: boolean | null
  ): LaundryMachineProgram[] {
    if (machine == null || machine.programs == null) {
      return [];
    }
    return machine.programs.filter(
      p =>
        (program == null || p.name === program) &&
        (subprogram == null || p.subprogram === subprogram) &&
        (spin == null || p.spin === spin) &&
        (preWash == null || p.preWash === preWash) &&
        (protect == null || p.protect === protect)
    );
  }

  findSingleMatchingProgram(
    program?: string | null,
    subprogram?: string | null,
    spin?: number | null,
    preWash?: boolean | null,
    protect?: boolean | null
  ): LaundryMachineProgram {
    if (this.selectedMachine == null) {
      throw new Error('no_machine_selected');
    }
    const programs: LaundryMachineProgram[] = this.selectedMachine.programs!.filter(
      p =>
        (p.name == null && program == null) ||
        (p.name === program &&
          ((p.subprogram == null && subprogram == null) || p.subprogram === subprogram) &&
          ((p.spin == null && spin == null) || p.spin === spin) &&
          ((p.preWash == null && !preWash) || p.preWash === preWash) &&
          ((p.protect == null && !protect) || p.protect === protect))
    );
    if (programs.length === 0) {
      throw new Error('no_matchin_program');
    }
    if (programs.length > 1) {
      throw new Error('multiple_matching_programs');
    }
    return programs[0];
  }

  isShowProgramSelect(): boolean {
    return this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.valid;
  }

  isShowSubprogramSelect(): boolean {
    return this.isShowProgramSelect() && this.formGroup.get(FormProperties.PROGRAM)!.valid && this.subprograms.length > 0;
  }

  isShowSpinSelect(): boolean {
    if (
      this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.valid &&
      this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.value.type === LaundryMachineType.WASHING_MACHINE &&
      this.formGroup.get(FormProperties.PROGRAM)!.valid
    ) {
      return (!this.isShowSubprogramSelect() || this.formGroup.get(FormProperties.SUBPROGRAM)!.valid) && this.spins.length > 0;
    }
    return false;
  }

  isShowPreWashCheckbox(): boolean {
    if (
      this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.valid &&
      this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.value.type === LaundryMachineType.WASHING_MACHINE &&
      this.formGroup.get(FormProperties.PROGRAM)!.valid
    ) {
      return (
        (!this.isShowSubprogramSelect() || this.formGroup.get(FormProperties.SUBPROGRAM)!.valid) &&
        (!this.isShowSpinSelect() || this.formGroup.get(FormProperties.SPIN)!.valid)
      );
    }
    return false;
  }

  isShowProtectCheckbox(): boolean {
    if (
      this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.valid &&
      this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.value.type === LaundryMachineType.DRYER &&
      this.formGroup.get(FormProperties.PROGRAM)!.valid
    ) {
      if (!this.isShowSubprogramSelect() || this.formGroup.get(FormProperties.SUBPROGRAM)!.valid) {
        return (
          this.filterPrograms(this.selectedMachine, this.selectedProgram, this.selectedSubprogram).filter(p =>
            WashingComponent.notEmpty(p.protect)
          ).length > 0
        );
      }
    }
    return false;
  }

  update(): void {
    this.updatePossibleValues();

    this.setFormControlStatus(
      FormProperties.PROGRAM,
      this.isShowProgramSelect(),
      null,
      this.selectedProgram != null && this.programs.includes(this.selectedProgram) ? this.selectedProgram : null
    );
    this.setFormControlStatus(
      FormProperties.SUBPROGRAM,
      this.isShowSubprogramSelect(),
      null,
      this.selectedSubprogram != null && this.subprograms.includes(this.selectedSubprogram) ? this.selectedSubprogram : null
    );
    this.setFormControlStatus(
      FormProperties.SPIN,
      this.isShowSpinSelect(),
      this.selectedSpin != null && this.spins.includes(this.selectedSpin) ? this.selectedSpin : null
    );
    this.setFormControlStatus(FormProperties.PREWASH, this.isShowPreWashCheckbox(), null, !!this.selectedPreWash);
    this.setFormControlStatus(FormProperties.PROTECT, this.isShowProtectCheckbox(), null, !!this.selectedProtect);

    // TODO: Otherwise changed after checked error
    this.cd.detectChanges();
  }

  updatePossibleValues(): void {
    this.programs = this.filterPrograms(this.selectedMachine)
      .map((laundryMachineProgram: LaundryMachineProgram) => laundryMachineProgram.name)
      .filter(name => !_.isNil(name)) as string[];
    this.programs = _.uniq(this.programs);
    this.subprograms = this.filterPrograms(this.selectedMachine, this.selectedProgram)
      .map((laundryMachineProgram: LaundryMachineProgram) => laundryMachineProgram.subprogram)
      .filter(name => !_.isNil(name)) as string[];
    this.subprograms = _.uniq(this.subprograms).sort();
    this.spins = this.filterPrograms(this.selectedMachine, this.selectedProgram, this.selectedSubprogram)
      .map((laundryMachineProgram: LaundryMachineProgram) => laundryMachineProgram.spin)
      .filter(name => !_.isNil(name)) as number[];
    this.spins = _.uniq(this.spins).sort();
  }

  translateMachineName(value: LaundryMachine | number): string | null {
    if (typeof value === 'number') {
      let machine;
      for (const m of this.machines) {
        if (m.id === value) {
          machine = m;
          break;
        }
      }
      if (machine === undefined) {
        return null;
      }
      value = machine;
    }

    const machineType: string = this.translateService.instant('washing.machineTypes.' + value.type!);
    return `${machineType} ${Number(value.id!)}`;
  }

  private getSubprogramsForProgram(program: string): string[] {
    if (this.selectedMachine == null) {
      return [];
    }
    return this.selectedMachine
      .programs!.filter(p => p.name === program)
      .map(p => p.subprogram)
      .filter(WashingComponent.notEmpty)
      .filter(WashingComponent.onlyUnique);
  }

  private setFormControlStatus(propertyName: FormProperties, visible: boolean, valueOnDisable?: any, valueOnEnable?: any): void {
    const control = this.formGroup.get(propertyName);
    if (visible) {
      control?.enable({ emitEvent: false, onlySelf: true });
      if (valueOnEnable !== undefined) {
        control?.patchValue(valueOnEnable, { emitEvent: false, onlySelf: true });
      }
    } else {
      control?.disable({ emitEvent: false, onlySelf: true });
      if (valueOnDisable !== undefined) {
        control?.patchValue(valueOnDisable, { emitEvent: false, onlySelf: true });
      }
    }
  }
}
