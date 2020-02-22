import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['registration.component.scss']
})
export class RegistrationComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'Registration message';
  }

  ngOnInit(): void {}
}
