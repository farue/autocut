import { Component } from '@angular/core';
import { WashingService } from 'app/services/washing/washing.service';
import { isEqual } from 'lodash-es';
import { Observable, of, timer } from 'rxjs';
import { catchError, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Machine } from 'app/entities/washing/washing.model';
import { LoggedInUserService } from 'app/shared/service/logged-in-user.service';

@Component({
  selector: 'jhi-services-overview',
  templateUrl: './services-overview.component.html',
  styleUrls: ['./services-overview.component.scss'],
})
export class ServicesOverviewComponent {
  machines: Machine[] = [];
  machines$: Observable<Machine[]> = timer(0, 60000).pipe(
    switchMap(v =>
      // create inner observable to continue outer observable on errors
      of(v).pipe(
        switchMap(() => this.washingService.getAllMachines()),
        catchError(err => {
          console.error(err);
          return of(this.machines);
        })
      )
    ),
    distinctUntilChanged(isEqual)
  );

  networkStatus$ = this.loggedInUserService.networkStatus();

  transactionBooks$ = this.loggedInUserService.transactionBooks();

  constructor(private washingService: WashingService, private loggedInUserService: LoggedInUserService) {}
}
