import { AbstractControl, AsyncValidator, ValidationErrors } from '@angular/forms';
import { Injectable } from '@angular/core';
import { Observable, timer } from 'rxjs';
import { finalize, first, map, switchMap } from 'rxjs/operators';
import { UserService } from 'app/entities/user/user.service';

@Injectable({ providedIn: 'root' })
export class UsernameValidator implements AsyncValidator {
  loading = false;

  constructor(private userService: UserService) {}

  validate(control: AbstractControl): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
    this.loading = true;
    return timer(500).pipe(
      switchMap(() =>
        this.userService.usernameExists(control.value).pipe(
          first(),
          map((exists: boolean) => (exists ? { usernameExists: true } : null)),
          finalize(() => (this.loading = false))
        )
      )
    );
  }
}
