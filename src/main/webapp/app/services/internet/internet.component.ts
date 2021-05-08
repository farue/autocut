import { Component } from '@angular/core';
import { LoggedInUserService } from 'app/shared/service/logged-in-user.service';
import { map } from 'rxjs/operators';
import { Lease } from 'app/entities/lease/lease.model';

@Component({
  selector: 'jhi-service-transaction',
  templateUrl: './internet.component.html',
  styleUrls: ['./internet.component.scss'],
})
export class InternetComponent {
  displayedColumns: string[] = ['key', 'value'];
  dataSource1 = [
    { key: 'internet.ipData.ip', value: undefined },
    { key: 'internet.ipData.subnetMask', value: '255.255.255.0' },
    { key: 'internet.ipData.gateway', value: '137.226.153.1' },
    { key: 'internet.ipData.dns1', value: '134.130.4.1' },
    { key: 'internet.ipData.dns2', value: '134.130.5.1' },
    { key: 'internet.ipData.dns3', value: '8.8.8.8' },
  ];

  error = false;

  constructor(private loggedInUserService: LoggedInUserService) {
    this.loggedInUserService
      .lease()
      .pipe(map((lease: Lease) => lease.apartment?.internetAccess?.ip1))
      .subscribe(
        (ip: string | undefined) => (this.dataSource1[0].value = ip),
        () => (this.error = true)
      );
  }
}
