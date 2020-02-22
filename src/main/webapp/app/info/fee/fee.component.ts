import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-fee',
  templateUrl: './fee.component.html',
  styleUrls: ['fee.component.scss']
})
export class FeeComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'Fee message';
  }

  ngOnInit(): void {}
}
