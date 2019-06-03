import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-janitor',
  templateUrl: './janitor.component.html',
  styleUrls: ['janitor.component.scss']
})
export class JanitorComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'JanitorComponent message';
  }

  ngOnInit() {}
}
