import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';
import { LaundryMachineService } from './laundry-machine.service';
import { LaundryMachineDeleteDialogComponent } from './laundry-machine-delete-dialog.component';

@Component({
  selector: 'jhi-laundry-machine',
  templateUrl: './laundry-machine.component.html'
})
export class LaundryMachineComponent implements OnInit, OnDestroy {
  laundryMachines?: ILaundryMachine[];
  eventSubscriber?: Subscription;

  constructor(
    protected laundryMachineService: LaundryMachineService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.laundryMachineService.query().subscribe((res: HttpResponse<ILaundryMachine[]>) => (this.laundryMachines = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInLaundryMachines();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ILaundryMachine): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInLaundryMachines(): void {
    this.eventSubscriber = this.eventManager.subscribe('laundryMachineListModification', () => this.loadAll());
  }

  delete(laundryMachine: ILaundryMachine): void {
    const modalRef = this.modalService.open(LaundryMachineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.laundryMachine = laundryMachine;
  }
}
