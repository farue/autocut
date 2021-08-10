import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { BroadcastMessage, IBroadcastMessage } from '../broadcast-message.model';
import { BroadcastMessageService } from '../service/broadcast-message.service';

@Component({
  selector: 'jhi-broadcast-message-update',
  templateUrl: './broadcast-message-update.component.html',
})
export class BroadcastMessageUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    start: [],
    end: [],
    usersOnly: [],
    dismissible: [],
  });

  constructor(
    protected broadcastMessageService: BroadcastMessageService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ broadcastMessage }) => {
      if (broadcastMessage.id === undefined) {
        const today = dayjs().startOf('day');
        broadcastMessage.start = today;
        broadcastMessage.end = today;
      }

      this.updateForm(broadcastMessage);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const broadcastMessage = this.createFromForm();
    if (broadcastMessage.id !== undefined) {
      this.subscribeToSaveResponse(this.broadcastMessageService.update(broadcastMessage));
    } else {
      this.subscribeToSaveResponse(this.broadcastMessageService.create(broadcastMessage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBroadcastMessage>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(broadcastMessage: IBroadcastMessage): void {
    this.editForm.patchValue({
      id: broadcastMessage.id,
      type: broadcastMessage.type,
      start: broadcastMessage.start ? broadcastMessage.start.format(DATE_TIME_FORMAT) : null,
      end: broadcastMessage.end ? broadcastMessage.end.format(DATE_TIME_FORMAT) : null,
      usersOnly: broadcastMessage.usersOnly,
      dismissible: broadcastMessage.dismissible,
    });
  }

  protected createFromForm(): IBroadcastMessage {
    return {
      ...new BroadcastMessage(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      start: this.editForm.get(['start'])!.value ? dayjs(this.editForm.get(['start'])!.value, DATE_TIME_FORMAT) : undefined,
      end: this.editForm.get(['end'])!.value ? dayjs(this.editForm.get(['end'])!.value, DATE_TIME_FORMAT) : undefined,
      usersOnly: this.editForm.get(['usersOnly'])!.value,
      dismissible: this.editForm.get(['dismissible'])!.value,
    };
  }
}
