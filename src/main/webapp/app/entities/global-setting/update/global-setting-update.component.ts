import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGlobalSetting, GlobalSetting } from '../global-setting.model';
import { GlobalSettingService } from '../service/global-setting.service';

@Component({
  selector: 'jhi-global-setting-update',
  templateUrl: './global-setting-update.component.html',
})
export class GlobalSettingUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    key: [],
    value: [],
    valueType: [],
  });

  constructor(protected globalSettingService: GlobalSettingService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ globalSetting }) => {
      this.updateForm(globalSetting);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const globalSetting = this.createFromForm();
    if (globalSetting.id !== undefined) {
      this.subscribeToSaveResponse(this.globalSettingService.update(globalSetting));
    } else {
      this.subscribeToSaveResponse(this.globalSettingService.create(globalSetting));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGlobalSetting>>): void {
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

  protected updateForm(globalSetting: IGlobalSetting): void {
    this.editForm.patchValue({
      id: globalSetting.id,
      key: globalSetting.key,
      value: globalSetting.value,
      valueType: globalSetting.valueType,
    });
  }

  protected createFromForm(): IGlobalSetting {
    return {
      ...new GlobalSetting(),
      id: this.editForm.get(['id'])!.value,
      key: this.editForm.get(['key'])!.value,
      value: this.editForm.get(['value'])!.value,
      valueType: this.editForm.get(['valueType'])!.value,
    };
  }
}
