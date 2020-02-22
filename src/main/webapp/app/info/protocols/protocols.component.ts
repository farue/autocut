import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-protocols',
  templateUrl: './protocols.component.html',
  styleUrls: ['protocols.component.scss']
})
export class ProtocolsComponent implements OnInit {
  message: string;

  constructor() {
    this.message = 'Protocols message';
  }

  ngOnInit(): void {}
}
