import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILaundryMachine } from '../laundry-machine.model';
import { LaundryMachineService } from '../service/laundry-machine.service';
import { LaundryMachineDeleteDialogComponent } from '../delete/laundry-machine-delete-dialog.component';

@Component({
  selector: 'jhi-laundry-machine',
  templateUrl: './laundry-machine.component.html',
})
export class LaundryMachineComponent implements OnInit {
  laundryMachines?: ILaundryMachine[];
  isLoading = false;

  constructor(protected laundryMachineService: LaundryMachineService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.laundryMachineService.query().subscribe(
      (res: HttpResponse<ILaundryMachine[]>) => {
        this.isLoading = false;
        this.laundryMachines = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ILaundryMachine): number {
    return item.id!;
  }

  delete(laundryMachine: ILaundryMachine): void {
    const modalRef = this.modalService.open(LaundryMachineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.laundryMachine = laundryMachine;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
