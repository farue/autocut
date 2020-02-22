import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-contacts',
  templateUrl: './contacts.component.html',
  styleUrls: ['contacts.component.scss']
})
export class ContactsComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'Contacts message';
  }

  ngOnInit(): void {}
}
