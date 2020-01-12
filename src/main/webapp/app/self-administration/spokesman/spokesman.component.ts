import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-spokesman',
  templateUrl: './spokesman.component.html',
  styleUrls: ['spokesman.component.scss']
})
export class SpokesmanComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'SpokesmanComponent message';
  }

  ngOnInit(): void {}
}
