import { Component } from '@angular/core';
import { WashingService } from 'app/services/washing/washing.service';

@Component({
  selector: 'jhi-services-overview',
  templateUrl: './services-overview.component.html',
  styleUrls: ['./services-overview.component.scss'],
})
export class ServicesOverviewComponent {
  machines$ = this.washingService.getAllMachines();

  constructor(private washingService: WashingService) {}
}
