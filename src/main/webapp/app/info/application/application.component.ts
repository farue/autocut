import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-application',
  templateUrl: './application.component.html',
  styleUrls: ['application.component.scss']
})
export class ApplicationComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'Application message';
  }

  ngOnInit(): void {}
}
