import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-info',
  templateUrl: './info.component.html',
  styleUrls: ['info.component.scss']
})
export class InfoComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'InfoComponent message';
  }

  ngOnInit(): void {}
}
