import { Component } from '@angular/core';
import { LoggedInUserService } from '../../shared/service/logged-in-user.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'jhi-service-transaction',
  templateUrl: './internet.component.html',
  styleUrls: ['internet.component.scss'],
})
export class InternetComponent {
  ip$ = this.loggedInUserService.lease().pipe(map(lease => lease?.apartment?.internetAccess?.ip1));

  constructor(private loggedInUserService: LoggedInUserService) {}
}
