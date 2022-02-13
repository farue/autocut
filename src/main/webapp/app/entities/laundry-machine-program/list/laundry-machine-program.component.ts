import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILaundryMachineProgram } from '../laundry-machine-program.model';
import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';
import { LaundryMachineProgramDeleteDialogComponent } from '../delete/laundry-machine-program-delete-dialog.component';

@Component({
  selector: 'jhi-laundry-machine-program',
  templateUrl: './laundry-machine-program.component.html',
})
export class LaundryMachineProgramComponent implements OnInit {
  laundryMachinePrograms?: ILaundryMachineProgram[];
  isLoading = false;

  constructor(protected laundryMachineProgramService: LaundryMachineProgramService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.laundryMachineProgramService.query().subscribe({
      next: (res: HttpResponse<ILaundryMachineProgram[]>) => {
        this.isLoading = false;
        this.laundryMachinePrograms = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ILaundryMachineProgram): number {
    return item.id!;
  }

  delete(laundryMachineProgram: ILaundryMachineProgram): void {
    const modalRef = this.modalService.open(LaundryMachineProgramDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.laundryMachineProgram = laundryMachineProgram;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
