import { Component } from '@angular/core';
import { ContactsEnum } from 'app/contact/contacts.enum';

@Component({
  selector: 'jhi-contacts',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss'],
})
export class ContactComponent {
  message = 'Contacts message';

  contacts = [
    {
      name: ContactsEnum.SPOKESPERSON,
      requireLogin: false,
    },
    {
      name: ContactsEnum.ASSIGNMENT_TEAM,
      requireLogin: false,
    },
    {
      name: ContactsEnum.NETWORKING_TEAM,
      requireLogin: false,
    },
    {
      name: ContactsEnum.WASHING_TEAM,
      requireLogin: true,
    },
    {
      name: ContactsEnum.TOOLS_TEAM,
      requireLogin: true,
    },
    {
      name: ContactsEnum.JANITOR,
      requireLogin: true,
    },
  ];
}
