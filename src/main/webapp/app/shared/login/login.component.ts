import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Router } from '@angular/router';

import { LoginService } from 'app/core/login/login.service';
import { HttpErrorResponse } from '@angular/common/http';
import { USER_NOT_ACTIVATED_TYPE, USER_NOT_VERIFIED_TYPE } from 'app/shared/constants/error.constants';

@Component({
  selector: 'jhi-login-modal',
  templateUrl: './login.component.html',
})
export class LoginModalComponent implements AfterViewInit {
  @ViewChild('username', { static: false })
  username?: ElementRef;

  authenticationError = false;
  errorUserNotActivated = false;
  errorUserNotVerified = false;

  loginForm = this.fb.group({
    username: [''],
    password: [''],
    rememberMe: [false],
  });

  constructor(private loginService: LoginService, private router: Router, public activeModal: NgbActiveModal, private fb: FormBuilder) {}

  ngAfterViewInit(): void {
    if (this.username) {
      this.username.nativeElement.focus();
    }
  }

  cancel(): void {
    this.authenticationError = false;
    this.errorUserNotActivated = false;
    this.errorUserNotVerified = false;
    this.loginForm.patchValue({
      username: '',
      password: '',
    });
    this.activeModal.dismiss('cancel');
  }

  login(): void {
    this.loginService
      .login({
        username: this.loginForm.get('username')!.value,
        password: this.loginForm.get('password')!.value,
        rememberMe: this.loginForm.get('rememberMe')!.value,
      })
      .subscribe(
        () => {
          this.authenticationError = false;
          this.errorUserNotActivated = false;
          this.errorUserNotVerified = false;
          this.activeModal.close();
          if (
            this.router.url === '/account/register' ||
            this.router.url.startsWith('/account/activate') ||
            this.router.url.startsWith('/account/reset/')
          ) {
            this.router.navigate(['']);
          }
        },
        response => this.processError(response)
      );
  }

  register(): void {
    this.activeModal.dismiss('to state register');
    this.router.navigate(['/account/register']);
  }

  requestResetPassword(): void {
    this.activeModal.dismiss('to state requestReset');
    this.router.navigate(['/account/reset', 'request']);
  }

  private processError(response: HttpErrorResponse): void {
    this.authenticationError = false;
    this.errorUserNotActivated = false;
    this.errorUserNotVerified = false;

    if (response.status === 401 && response.error.type === USER_NOT_ACTIVATED_TYPE) {
      this.errorUserNotActivated = true;
    } else if (response.status === 401 && response.error.type === USER_NOT_VERIFIED_TYPE) {
      this.errorUserNotVerified = true;
    } else {
      this.authenticationError = true;
    }
  }
}
