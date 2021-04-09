import { Component } from '@angular/core';

@Component({
  selector: 'jhi-application',
  templateUrl: './application.component.html',
  styleUrls: ['./application.component.scss'],
})
export class ApplicationComponent {
  message: string;

  constructor() {
    this.message = 'Application message';
  }
}
