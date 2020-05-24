import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { Activity, IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from './activity.service';
import { ITenant } from 'app/shared/model/tenant.model';
import { TenantService } from 'app/entities/tenant/tenant.service';

@Component({
  selector: 'jhi-activity-update',
  templateUrl: './activity-update.component.html',
})
export class ActivityUpdateComponent implements OnInit {
  isSaving = false;
  tenants: ITenant[] = [];

  editForm = this.fb.group({
    id: [],
    year: [null, [Validators.required]],
    term: [null, [Validators.required]],
    startDate: [],
    endDate: [],
    description: [],
    discount: [],
    stwActivity: [],
    tenant: [],
  });

  constructor(
    protected activityService: ActivityService,
    protected tenantService: TenantService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activity }) => {
      if (!activity.id) {
        const today = moment().startOf('day');
        activity.startDate = today;
        activity.endDate = today;
      }

      this.updateForm(activity);

      this.tenantService.query().subscribe((res: HttpResponse<ITenant[]>) => (this.tenants = res.body || []));
    });
  }

  updateForm(activity: IActivity): void {
    this.editForm.patchValue({
      id: activity.id,
      year: activity.year,
      term: activity.term,
      startDate: activity.startDate ? activity.startDate.format(DATE_TIME_FORMAT) : null,
      endDate: activity.endDate ? activity.endDate.format(DATE_TIME_FORMAT) : null,
      description: activity.description,
      discount: activity.discount,
      stwActivity: activity.stwActivity,
      tenant: activity.tenant,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const activity = this.createFromForm();
    if (activity.id !== undefined) {
      this.subscribeToSaveResponse(this.activityService.update(activity));
    } else {
      this.subscribeToSaveResponse(this.activityService.create(activity));
    }
  }

  private createFromForm(): IActivity {
    return {
      ...new Activity(),
      id: this.editForm.get(['id'])!.value,
      year: this.editForm.get(['year'])!.value,
      term: this.editForm.get(['term'])!.value,
      startDate: this.editForm.get(['startDate'])!.value ? moment(this.editForm.get(['startDate'])!.value, DATE_TIME_FORMAT) : undefined,
      endDate: this.editForm.get(['endDate'])!.value ? moment(this.editForm.get(['endDate'])!.value, DATE_TIME_FORMAT) : undefined,
      description: this.editForm.get(['description'])!.value,
      discount: this.editForm.get(['discount'])!.value,
      stwActivity: this.editForm.get(['stwActivity'])!.value,
      tenant: this.editForm.get(['tenant'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: ITenant): any {
    return item.id;
  }
}
