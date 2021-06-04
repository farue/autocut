import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivateResponse, WashingService } from 'app/services/washing/washing.service';
import { LaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { HttpErrorResponse } from '@angular/common/http';
import { INSUFFICIENT_FUNDS_TYPE, LAUNDRY_MACHINE_UNAVAILABLE_TYPE } from 'app/config/error.constants';
import { LaundryMachineType } from 'app/entities/enumerations/laundry-machine-type.model';
import { FormBuilder, Validators } from '@angular/forms';
import { catchError, finalize, switchMap, takeUntil, tap } from 'rxjs/operators';
import * as dayjs from 'dayjs';
import { TranslateService } from '@ngx-translate/core';
import { bounceOnEnterAnimation, rotateAnimation } from 'angular-animations';
import { isNil, uniq } from 'lodash-es';
import { of, Subject, throwError } from 'rxjs';
import { Machine, Program } from 'app/entities/washing/washing.model';

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
  animations: [bounceOnEnterAnimation(), rotateAnimation()],
})
export class WashingComponent implements OnInit, OnDestroy {
  onDestroy$ = new Subject<void>();

  FormProperties = FormProperties;

  machines: Machine[] = [];
  machinePrograms: Program[] = [];

  // filtered values based on form inputs
  programs: string[] = [];
  subprograms: string[] = [];
  spins: number[] = [];

  initError = false;

  loadingMachines = false;
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

  rotateMachines = false;

  constructor(
    private fb: FormBuilder,
    private washingService: WashingService,
    private translateService: TranslateService,
    private cd: ChangeDetectorRef
  ) {}

  private static notEmpty<TValue>(value: TValue | null | undefined): value is TValue {
    return value !== null && value !== undefined;
  }

  private static onlyUnique<TValue>(value: TValue, index: number, self: TValue[]): boolean {
    return self.indexOf(value) === index;
  }

  ngOnInit(): void {
    this.loadingMachines = true;
    this.washingService
      .getAllMachines()
      .pipe(finalize(() => (this.loadingMachines = false)))
      .subscribe(
        (machines: Machine[]) => {
          this.initError = false;
          this.machines = machines;
        },
        () => (this.initError = true)
      );

    this.formGroup
      .get(FormProperties.LAUNDRY_MACHINE)!
      .valueChanges.pipe(
        takeUntil(this.onDestroy$),
        switchMap(machine => (machine ? this.washingService.getPrograms(machine) : of([]))),
        tap((programs: Program[]) => (this.machinePrograms = programs)),
        tap(() => this.update())
      )
      .subscribe();
    this.formGroup.valueChanges.subscribe(() => this.update());
  }

  ngOnDestroy(): void {
    this.onDestroy$.next();
    this.onDestroy$.complete();
  }

  get selectedMachine(): Machine | null {
    return this.formGroup.get(FormProperties.LAUNDRY_MACHINE)!.value as Machine | null;
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

  unlockAction(): void {
    const selectedProgram = this.getSelectedProgram();
    if (this.selectedMachine != null) {
      this.unlock(this.selectedMachine, selectedProgram);
    }
  }

  unlock(machine: Machine, program: Program): void {
    this.loadingActivate = true;
    this.response = undefined;

    this.washingService
      .unlock(machine, program)
      .pipe(
        tap((response: ActivateResponse) => {
          this.response = {
            ...response,
            countdownSeconds: response.endActivationTime.diff(dayjs(), 'seconds'),
            success: true,
          };
        }),
        switchMap(() => this.washingService.getAllMachines()),
        tap((machines: Machine[]) => (this.machines = machines)),
        finalize(() => (this.loadingActivate = false)),
        catchError((err: HttpErrorResponse) => {
          this.response = { success: false, machineId: machine.id };
          if (err.status === 400 && err.error.type === INSUFFICIENT_FUNDS_TYPE) {
            this.response.errorInsufficientFunds = true;
          } else if (err.status === 400 && err.error.type === LAUNDRY_MACHINE_UNAVAILABLE_TYPE) {
            this.response.errorMachineUnavailable = true;
          } else {
            this.response.genericError = true;
          }
          return throwError(err);
        })
      )
      .subscribe();
  }

  onCountdownFinished(): void {
    if (this.response?.countdownSeconds) {
      this.response.countdownSeconds = undefined;
    }
  }

  getSelectedProgram(): Program {
    return this.findSingleMatchingProgram(
      this.selectedProgram,
      this.selectedSubprogram,
      this.selectedSpin,
      this.selectedPreWash,
      this.selectedProtect
    );
  }

  filterPrograms(
    machine?: Machine | null,
    program?: string | null,
    subprogram?: string | null,
    spin?: number | null,
    preWash?: boolean | null,
    protect?: boolean | null
  ): Program[] {
    return this.machinePrograms.filter(
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
  ): Program {
    if (this.selectedMachine == null) {
      throw new Error('no_machine_selected');
    }
    const programs: Program[] = this.machinePrograms.filter(
      p =>
        p.name === program &&
        ((p.subprogram == null && subprogram == null) || p.subprogram === subprogram) &&
        ((p.spin == null && spin == null) || p.spin === spin) &&
        ((p.preWash == null && !preWash) || p.preWash === preWash) &&
        ((p.protect == null && !protect) || p.protect === protect)
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
    this.programs = this.filterPrograms(this.selectedMachine).map((program: Program) => program.name);
    this.programs = uniq(this.programs);
    this.subprograms = this.filterPrograms(this.selectedMachine, this.selectedProgram)
      .map((program: Program) => program.subprogram)
      .filter(name => !isNil(name)) as string[];
    this.subprograms = uniq(this.subprograms).sort();
    this.spins = this.filterPrograms(this.selectedMachine, this.selectedProgram, this.selectedSubprogram)
      .map((program: Program) => program.spin)
      .filter(name => !isNil(name)) as number[];
    this.spins = uniq(this.spins).sort();
  }

  findMachineById(id: number): LaundryMachine | null {
    for (const machine of this.machines) {
      if (machine.id === id) {
        return machine;
      }
    }
    return null;
  }

  timeRemaining(inUseUntil?: dayjs.Dayjs): number | null {
    if (!inUseUntil) {
      return null;
    }
    const timeRemaining = inUseUntil.diff(dayjs(), 'minutes');
    if (timeRemaining < 0) {
      return null;
    }
    return timeRemaining;
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
