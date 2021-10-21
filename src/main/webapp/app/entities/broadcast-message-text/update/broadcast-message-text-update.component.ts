import {Component, OnInit} from '@angular/core';
import {HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs';
import {finalize, map} from 'rxjs/operators';

import {BroadcastMessageText, IBroadcastMessageText} from '../broadcast-message-text.model';
import {BroadcastMessageTextService} from '../service/broadcast-message-text.service';
import {IBroadcastMessage} from 'app/entities/broadcast-message/broadcast-message.model';
import {BroadcastMessageService} from 'app/entities/broadcast-message/service/broadcast-message.service';

@Component({
  selector: 'jhi-broadcast-message-text-update',
  templateUrl: './broadcast-message-text-update.component.html',
})
export class BroadcastMessageTextUpdateComponent implements OnInit {
  isSaving = false;

  broadcastMessagesSharedCollection: IBroadcastMessage[] = [];

  editForm = this.fb.group({
    id: [],
    langKey: [null, [Validators.required, Validators.maxLength(2)]],
    text: [null, [Validators.required, Validators.maxLength(4000)]],
    message: [null, Validators.required],
  });

  constructor(
    protected broadcastMessageTextService: BroadcastMessageTextService,
    protected broadcastMessageService: BroadcastMessageService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ broadcastMessageText }) => {
      this.updateForm(broadcastMessageText);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const broadcastMessageText = this.createFromForm();
    if (broadcastMessageText.id !== undefined) {
      this.subscribeToSaveResponse(this.broadcastMessageTextService.update(broadcastMessageText));
    } else {
      this.subscribeToSaveResponse(this.broadcastMessageTextService.create(broadcastMessageText));
    }
  }

  trackBroadcastMessageById(index: number, item: IBroadcastMessage): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBroadcastMessageText>>): void {
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

  protected updateForm(broadcastMessageText: IBroadcastMessageText): void {
    this.editForm.patchValue({
      id: broadcastMessageText.id,
      langKey: broadcastMessageText.langKey,
      text: broadcastMessageText.text,
      message: broadcastMessageText.message,
    });

    this.broadcastMessagesSharedCollection = this.broadcastMessageService.addBroadcastMessageToCollectionIfMissing(
      this.broadcastMessagesSharedCollection,
      broadcastMessageText.message
    );
  }

  protected loadRelationshipsOptions(): void {
    this.broadcastMessageService
      .query()
      .pipe(map((res: HttpResponse<IBroadcastMessage[]>) => res.body ?? []))
      .pipe(
        map((broadcastMessages: IBroadcastMessage[]) =>
          this.broadcastMessageService.addBroadcastMessageToCollectionIfMissing(broadcastMessages, this.editForm.get('message')!.value)
        )
      )
      .subscribe((broadcastMessages: IBroadcastMessage[]) => (this.broadcastMessagesSharedCollection = broadcastMessages));
  }

  protected createFromForm(): IBroadcastMessageText {
    return {
      ...new BroadcastMessageText(),
      id: this.editForm.get(['id'])!.value,
      langKey: this.editForm.get(['langKey'])!.value,
      text: this.editForm.get(['text'])!.value,
      message: this.editForm.get(['message'])!.value,
    };
  }
}
