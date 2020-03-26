import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { LaundryMachineProgramService } from './laundry-machine-program.service';
import { LaundryMachineProgramDeleteDialogComponent } from './laundry-machine-program-delete-dialog.component';

@Component({
  selector: 'jhi-laundry-machine-program',
  templateUrl: './laundry-machine-program.component.html'
})
export class LaundryMachineProgramComponent implements OnInit, OnDestroy {
  laundryMachinePrograms?: ILaundryMachineProgram[];
  eventSubscriber?: Subscription;

  constructor(
    protected laundryMachineProgramService: LaundryMachineProgramService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadAll(): void {
    this.laundryMachineProgramService.query().subscribe((res: HttpResponse<ILaundryMachineProgram[]>) => {
      this.laundryMachinePrograms = res.body ? res.body : [];
    });
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInLaundryMachinePrograms();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ILaundryMachineProgram): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInLaundryMachinePrograms(): void {
    this.eventSubscriber = this.eventManager.subscribe('laundryMachineProgramListModification', () => this.loadAll());
  }

  delete(laundryMachineProgram: ILaundryMachineProgram): void {
    const modalRef = this.modalService.open(LaundryMachineProgramDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.laundryMachineProgram = laundryMachineProgram;
  }
}
