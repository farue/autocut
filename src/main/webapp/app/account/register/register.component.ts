import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { RegisterService } from './register.service';
import { IApartment } from 'app/entities/apartment/apartment.model';
import { ApartmentValidator } from 'app/account/register/apartment-validator';
import { ImmediateErrorStateMatcher } from 'app/shared/material/immediate-error-state-matcher';
import * as dayjs from 'dayjs';
import { OpUnitType } from 'dayjs';
import { BreakpointObserver } from '@angular/cdk/layout';
import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { heartBeatAnimation } from 'angular-animations';
import { MatStepper } from '@angular/material/stepper';
import { ErrorStateMatcher } from '@angular/material/core';
import { UsernameValidator } from 'app/account/register/username-validator';
import { EmailValidator } from 'app/account/register/email-validator';
import { MediaService } from 'app/shared/service/media.service';

export function dateNotAfter(value: number, unit: OpUnitType): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const date: dayjs.Dayjs | string | null = control.value;
    if (!date || typeof date === 'string') {
      // if date is of type string, it could not be parsed so other errors will be shown
      return null;
    }
    if (date.isAfter(dayjs().add(value, unit))) {
      return { invalidMaxDate: true };
    }
    return null;
  };
}

export function passwordsMatchValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');
    return password && confirmPassword && password.value !== confirmPassword.value ? { passwordMismatch: true } : null;
  };
}

export class PasswordMismatchErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null): boolean {
    return !!control?.touched && !!control.parent && control.parent.hasError('passwordMismatch');
  }
}

@Component({
  selector: 'jhi-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  animations: [heartBeatAnimation({ anchor: 'heartBeat', direction: '=>' })],
})
export class RegisterComponent implements AfterViewInit {
  @ViewChild('username', { static: false })
  firstName?: ElementRef;
  @ViewChild('stepper')
  stepper?: MatStepper;

  verticalStepper$ = this.mediaService.isLtMd$;
  selectedIndex = 0;
  animationEnd = false;
  loading = false;

  immediateErrorStateMatcher = new ImmediateErrorStateMatcher();
  passwordMismatchErrorStateMatcher = new PasswordMismatchErrorStateMatcher();
  success = false;
  apartment: IApartment | null = null;

  userForm = this.fb.group(
    {
      login: [
        '',
        [
          Validators.required,
          Validators.minLength(1),
          Validators.maxLength(50),
          Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
        ],
        [this.usernameValidator.validate.bind(this.usernameValidator)],
      ],
      email: [
        '',
        [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email],
        [this.emailValidator.validate.bind(this.emailValidator)],
      ],
      password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
      networkTerms: [false, [Validators.requiredTrue]],
      generalTerms: [false, [Validators.requiredTrue]],
    },
    { validators: passwordsMatchValidator() }
  );

  personForm = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    lastName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    apartment: [
      '',
      [Validators.required, Validators.pattern('\\d{2}-\\d{2}')],
      [this.apartmentValidator.validate.bind(this.apartmentValidator)],
    ],
    start: ['', [Validators.required, dateNotAfter(1, 'month')]],
    end: ['', [Validators.required]],
  });

  constructor(
    private fb: FormBuilder,
    private translateService: TranslateService,
    private registerService: RegisterService,
    public usernameValidator: UsernameValidator,
    public emailValidator: EmailValidator,
    public apartmentValidator: ApartmentValidator,
    private bpObserver: BreakpointObserver,
    private mediaService: MediaService
  ) {}

  ngAfterViewInit(): void {
    if (this.firstName) {
      this.firstName.nativeElement.focus();
    }
  }

  selectionChanged(event: StepperSelectionEvent): void {
    this.selectedIndex = event.selectedIndex;
    this.animationEnd = false;
  }

  register(): void {
    this.loading = true;
    const userData = this.userForm.getRawValue();
    const personData = this.personForm.getRawValue();
    this.registerService.save({ ...userData, ...personData, langKey: this.translateService.currentLang }).subscribe(
      () => {
        this.loading = false;
        this.success = true;
        this.stepper?.next();
      },
      () => {
        this.loading = false;
      }
    );
  }

  isUniversityEmail(email: string): boolean {
    if (!email) {
      return false;
    }
    return /[.@]rwth-aachen\.de$/.test(email) || /[.@]fh-aachen\.de$/.test(email);
  }

  getApartmentString(apartment: IApartment): string {
    if (apartment.address?.street && apartment.address.streetNumber && apartment.nr) {
      return `${apartment.address.street} ${apartment.address.streetNumber} Whg. ${apartment.nr}`;
    }
    return '';
  }
}
