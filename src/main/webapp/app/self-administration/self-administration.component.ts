import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-self-administration',
  templateUrl: './self-administration.component.html',
  styleUrls: ['self-administration.component.scss']
})
export class SelfAdministrationComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'SelfAdministrationComponent message';
  }

  ngOnInit() {}
}
