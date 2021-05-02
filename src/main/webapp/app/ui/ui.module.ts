import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CheckmarkComponent } from './checkmark/checkmark.component';
import { LoadingOverlayComponent } from './loading-overlay/loading-overlay.component';
import { ButtonLoadingDirective } from 'app/ui/loading-button/button-loading.directive';

@NgModule({
  declarations: [CheckmarkComponent, LoadingOverlayComponent, ButtonLoadingDirective],
  imports: [SharedModule],
  exports: [CheckmarkComponent, LoadingOverlayComponent, ButtonLoadingDirective],
})
export class UiModule {}
