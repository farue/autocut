import { Component } from '@angular/core';

@Component({
  selector: 'jhi-application',
  templateUrl: './imprint.component.html',
  styleUrls: ['./imprint.component.scss']
})
export class ImprintComponent {
  dataName = 'net';
  dataDomain = 'farue.rwth-aachen';
  dataTld = 'de';

  mailClicked(): boolean {
    window.location.href = 'mailto:' + this.dataName + '@' + this.dataDomain + '.' + this.dataTld;
    return false;
  }
}
