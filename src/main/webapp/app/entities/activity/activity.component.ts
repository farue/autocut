import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from './activity.service';
import { ActivityDeleteDialogComponent } from './activity-delete-dialog.component';

@Component({
  selector: 'jhi-activity',
  templateUrl: './activity.component.html',
})
export class ActivityComponent implements OnInit, OnDestroy {
  activities?: IActivity[];
  eventSubscriber?: Subscription;

  constructor(protected activityService: ActivityService, protected eventManager: JhiEventManager, protected modalService: NgbModal) {}

  loadAll(): void {
    this.activityService.query().subscribe((res: HttpResponse<IActivity[]>) => (this.activities = res.body || []));
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInActivities();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IActivity): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInActivities(): void {
    this.eventSubscriber = this.eventManager.subscribe('activityListModification', () => this.loadAll());
  }

  delete(activity: IActivity): void {
    const modalRef = this.modalService.open(ActivityDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.activity = activity;
  }
}
