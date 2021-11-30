import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ContactsEnum } from 'app/contact/contacts.enum';
import { FormBuilder, Validators } from '@angular/forms';
import { ContactFormPropertiesEnum } from 'app/contact/contact-form/contact-form-properties.enum';
import { MatFormField } from '@angular/material/form-field';
import { ContactService } from '../../entities/contact/contact.service';
import { ContactForm } from '../../entities/contact/contact.model';
import { LoggedInUserService } from '../../shared/service/logged-in-user.service';
import { AccountService } from '../../core/auth/account.service';
import { finalize, map, tap } from 'rxjs/operators';
import { forkJoin } from 'rxjs';
import { Tenant } from '../../entities/tenant/tenant.model';
import { Lease } from '../../entities/lease/lease.model';
import { User } from '../../admin/user-management/user-management.model';

@Component({
  selector: 'jhi-contact-form',
  templateUrl: './contact-form.component.html',
  styleUrls: ['./contact-form.component.scss'],
})
export class ContactFormComponent implements OnInit {
  static STUDENT_RESIDENCE = 'EPW';

  @ViewChild('subjectFormField') subjectInputComponent!: MatFormField;

  ContactsEnum = ContactsEnum;
  ContactFormPropertiesEnum = ContactFormPropertiesEnum;

  team: ContactsEnum;

  formGroup = this.fb.group({
    [ContactFormPropertiesEnum.NAME]: ['', [Validators.required]],
    [ContactFormPropertiesEnum.APARTMENT]: [''],
    [ContactFormPropertiesEnum.EMAIL]: ['', [Validators.required, Validators.email]],
    [ContactFormPropertiesEnum.SUBJECT]: ['', [Validators.required]],
    [ContactFormPropertiesEnum.MESSAGE]: ['', [Validators.required]],
    [ContactFormPropertiesEnum.COPY_TO_OWN_EMAIL]: [false, [Validators.required]],
  });

  name?: string;
  email?: string;
  apartment?: string;

  pending = false;
  success = false;
  error = false;

  constructor(
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private contactService: ContactService,
    private accountService: AccountService,
    private loggedInUserService: LoggedInUserService
  ) {
    if (route.snapshot.url.length === 0) {
      console.error('Could not determine group name for contact form. Did the routing change?');
    }
    this.team = route.snapshot.url[0].path as ContactsEnum;

    this.formGroup.get(ContactFormPropertiesEnum.APARTMENT)?.valueChanges.subscribe(() => this.subjectInputComponent.updateOutlineGap());
  }

  // eslint-disable-next-line @angular-eslint/no-empty-lifecycle-method
  ngOnInit(): void {
    if (this.accountService.isAuthenticated()) {
      forkJoin([this.loggedInUserService.user(), this.loggedInUserService.tenant(), this.loggedInUserService.lease()])
        .pipe(
          map<[User, Tenant, Lease], [string, string, string]>(([user, tenant, lease]: [User, Tenant, Lease]) => [
            user.email!,
            `${tenant.firstName!} ${tenant.lastName!}`,
            `${lease.apartment!.address!.streetNumber!}/${lease.apartment!.nr!}`,
          ]),
          tap(([email, name, apartment]: [string, string, string]) => {
            this.formGroup.get(ContactFormPropertiesEnum.NAME)!.setValue(name);
            this.formGroup.get(ContactFormPropertiesEnum.NAME)!.disable();
            this.formGroup.get(ContactFormPropertiesEnum.APARTMENT)!.setValue(apartment);
            this.formGroup.get(ContactFormPropertiesEnum.APARTMENT)!.disable();
            this.formGroup.get(ContactFormPropertiesEnum.EMAIL)!.setValue(email);
            this.formGroup.get(ContactFormPropertiesEnum.EMAIL)!.disable();
          })
        )
        .subscribe();

      this.loggedInUserService.user().pipe(
        map(user => user.email),
        tap()
      );
      this.loggedInUserService.tenant().pipe(tap(tenant => (this.name = `${tenant.firstName!} ${tenant.lastName!}`)));
      this.loggedInUserService
        .lease()
        .pipe(tap(lease => (this.apartment = `${lease.apartment!.address!.streetNumber!}/${lease.apartment!.nr!}`)));
    }
  }

  onSend(): void {
    this.success = false;
    this.error = false;
    this.pending = true;

    const contactForm = this.formGroup.getRawValue() as ContactForm;
    this.contactService
      .send(this.team, contactForm)
      .pipe(finalize(() => (this.pending = false)))
      .subscribe(
        () => (this.success = true),
        () => (this.error = true)
      );
  }

  getSubjectPrefix(): string {
    if (this.formGroup.get(ContactFormPropertiesEnum.APARTMENT)!.value) {
      return `${ContactFormComponent.STUDENT_RESIDENCE} ${this.formGroup.get(ContactFormPropertiesEnum.APARTMENT)!.value as string}: `;
    }
    return '';
  }
}
