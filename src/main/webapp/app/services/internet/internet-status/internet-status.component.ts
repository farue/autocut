import { Component, Input } from '@angular/core';
import { NetworkStatus } from 'app/entities/internet/network-status.model';

@Component({
  selector: 'jhi-internet-status',
  templateUrl: './internet-status.component.html',
  styleUrls: ['./internet-status.component.scss'],
})
export class InternetStatusComponent {
  @Input()
  networkStatus?: NetworkStatus;
}
