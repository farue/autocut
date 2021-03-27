import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';

import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from 'app/config/error.constants';
import { RegisterService } from './register.service';

@Component({
  selector: 'jhi-register',
  templateUrl: './register.component.html',
})
export class RegisterComponent implements AfterViewInit {
  @ViewChild('firstName', { static: false })
  firstName?: ElementRef;

  doNotMatch = false;
  error = false;
  errorEmailExists = false;
  errorUserExists = false;
  success = false;

  registerForm = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    lastName: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(50)]],
    apartment: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5), Validators.pattern('\\d{2}-\\d{2}')]],
    start: ['', [Validators.required]],
    end: ['', [Validators.required]],
    login: [
      '',
      [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern('^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$'),
      ],
    ],
    email: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(254), Validators.email]],
    password: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
    confirmPassword: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(50)]],
    networkTerms: [false, [Validators.requiredTrue]],
    generalTerms: [false, [Validators.requiredTrue]],
  });

  constructor(private translateService: TranslateService, private registerService: RegisterService, private fb: FormBuilder) {}

  ngAfterViewInit(): void {
    if (this.firstName) {
      this.firstName.nativeElement.focus();
    }
  }

  register(): void {
    this.doNotMatch = false;
    this.error = false;
    this.errorEmailExists = false;
    this.errorUserExists = false;

    const password = this.registerForm.get(['password'])!.value;
    if (password !== this.registerForm.get(['confirmPassword'])!.value) {
      this.doNotMatch = true;
    } else {
      const firstName = this.registerForm.get(['firstName'])!.value;
      const lastName = this.registerForm.get(['lastName'])!.value;
      const apartment = this.registerForm.get(['apartment'])!.value;
      const start = this.registerForm.get(['start'])!.value;
      const end = this.registerForm.get(['end'])!.value;
      const login = this.registerForm.get(['login'])!.value;
      const email = this.registerForm.get(['email'])!.value;
      this.registerService.save({ login, firstName, lastName, apartment, start, end, email, password, langKey: this.translateService.currentLang }).subscribe(
        () => (this.success = true),
        response => this.processError(response)
      );
    }
  }

  private processError(response: HttpErrorResponse): void {
    if (response.status === 400 && response.error.type === LOGIN_ALREADY_USED_TYPE) {
      this.errorUserExists = true;
    } else if (response.status === 400 && response.error.type === EMAIL_ALREADY_USED_TYPE) {
      this.errorEmailExists = true;
    } else {
      this.error = true;
    }
  }
}
