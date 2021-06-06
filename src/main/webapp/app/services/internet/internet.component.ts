import { Component, OnInit } from '@angular/core';
import { LoggedInUserService } from 'app/shared/service/logged-in-user.service';
import { finalize, map } from 'rxjs/operators';
import { Lease } from 'app/entities/lease/lease.model';
import { NetworkStatus } from 'app/entities/internet/network-status.model';
import { MediaService } from 'app/shared/service/media.service';

@Component({
  selector: 'jhi-service-transaction',
  templateUrl: './internet.component.html',
  styleUrls: ['./internet.component.scss'],
})
export class InternetComponent implements OnInit {
  dataSourceIpData = [
    { key: 'internet.ipData.ip', value: undefined },
    { key: 'internet.ipData.subnetMask', value: '255.255.255.0' },
    { key: 'internet.ipData.gateway', value: '137.226.153.1' },
    { key: 'internet.ipData.dns1', value: '134.130.4.1' },
    { key: 'internet.ipData.dns2', value: '134.130.5.1' },
    { key: 'internet.ipData.dns3', value: '8.8.8.8' },
  ];

  dataSourceStatusHelp = [{ status: 'connected' }, { status: 'notconnect' }, { status: 'disabled' }, { status: 'err-disabled' }];

  error = false;
  updatingStatus = false;

  networkStatus?: NetworkStatus;

  constructor(private loggedInUserService: LoggedInUserService, public mediaService: MediaService) {}

  ngOnInit(): void {
    this.loggedInUserService
      .lease()
      .pipe(map((lease: Lease) => lease.apartment?.internetAccess?.ip1))
      .subscribe(
        (ip: string | undefined) => (this.dataSourceIpData[0].value = ip),
        () => (this.error = true)
      );

    this.loggedInUserService.networkStatus().subscribe((networkStatus: NetworkStatus) => (this.networkStatus = networkStatus));
  }

  updateStatus(): void {
    this.updatingStatus = true;
    this.loggedInUserService
      .updateAndGetNetworkStatus()
      .pipe(finalize(() => (this.updatingStatus = false)))
      .subscribe((networkStatus: NetworkStatus) => (this.networkStatus = networkStatus));
  }
}
