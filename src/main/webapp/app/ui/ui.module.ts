import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CheckmarkComponent } from './checkmark/checkmark.component';
import { LoadingOverlayComponent } from './loading-overlay/loading-overlay.component';
import { ButtonLoadingDirective } from 'app/ui/loading-button/button-loading.directive';
import { CountdownComponent } from './countdown/countdown.component';

@NgModule({
  declarations: [CheckmarkComponent, LoadingOverlayComponent, ButtonLoadingDirective, CountdownComponent],
  imports: [SharedModule],
  exports: [CheckmarkComponent, LoadingOverlayComponent, ButtonLoadingDirective, CountdownComponent],
})
export class UiModule {}
