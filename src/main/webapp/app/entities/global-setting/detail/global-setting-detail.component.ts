import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGlobalSetting } from '../global-setting.model';

@Component({
  selector: 'jhi-global-setting-detail',
  templateUrl: './global-setting-detail.component.html',
})
export class GlobalSettingDetailComponent implements OnInit {
  globalSetting: IGlobalSetting | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ globalSetting }) => {
      this.globalSetting = globalSetting;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
