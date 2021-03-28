import { Component } from '@angular/core';
import { LoggedInUserService } from '../../shared/service/logged-in-user.service';
import { map } from 'rxjs/operators';
import { Lease } from "app/entities/lease/lease.model";

@Component({
  selector: 'jhi-service-transaction',
  templateUrl: './internet.component.html',
  styleUrls: ['./internet.component.scss'],
})
export class InternetComponent {
  ip$ = this.loggedInUserService.lease().pipe(map((lease: Lease) => lease.apartment?.internetAccess?.ip1));

  constructor(private loggedInUserService: LoggedInUserService) {}
}
