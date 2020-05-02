import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'jhi-application',
  templateUrl: './imprint.component.html',
  styleUrls: ['imprint.component.scss']
})
export class ImprintComponent implements OnInit {
  dataName = 'net';
  dataDomain = 'farue.rwth-aachen';
  dataTld = 'de';

  constructor() {}

  ngOnInit(): void {}

  mailClicked(): boolean {
    window.location.href = 'mailto:' + this.dataName + '@' + this.dataDomain + '.' + this.dataTld;
    return false;
  }
}
