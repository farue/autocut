import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ILaundryProgram } from '../laundry-program.model';
import { LaundryProgramService } from '../service/laundry-program.service';
import { LaundryProgramDeleteDialogComponent } from '../delete/laundry-program-delete-dialog.component';

@Component({
  selector: 'jhi-laundry-program',
  templateUrl: './laundry-program.component.html',
})
export class LaundryProgramComponent implements OnInit {
  laundryPrograms?: ILaundryProgram[];
  isLoading = false;

  constructor(protected laundryProgramService: LaundryProgramService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.laundryProgramService.query().subscribe({
      next: (res: HttpResponse<ILaundryProgram[]>) => {
        this.isLoading = false;
        this.laundryPrograms = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ILaundryProgram): number {
    return item.id!;
  }

  delete(laundryProgram: ILaundryProgram): void {
    const modalRef = this.modalService.open(LaundryProgramDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.laundryProgram = laundryProgram;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
