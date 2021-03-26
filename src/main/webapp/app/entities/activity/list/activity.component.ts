import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IActivity } from '../activity.model';
import { ActivityService } from '../service/activity.service';
import { ActivityDeleteDialogComponent } from '../delete/activity-delete-dialog.component';

@Component({
  selector: 'jhi-activity',
  templateUrl: './activity.component.html',
})
export class ActivityComponent implements OnInit {
  activities?: IActivity[];
  isLoading = false;

  constructor(protected activityService: ActivityService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.activityService.query().subscribe(
      (res: HttpResponse<IActivity[]>) => {
        this.isLoading = false;
        this.activities = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IActivity): number {
    return item.id!;
  }

  delete(activity: IActivity): void {
    const modalRef = this.modalService.open(ActivityDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.activity = activity;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
