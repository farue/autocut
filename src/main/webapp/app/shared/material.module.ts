import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatCarouselModule } from '@ngbmodule/material-carousel';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatRippleModule } from '@angular/material/core';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MatStepperModule } from '@angular/material/stepper';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDayjsDateModule } from './material/dayjs-date.module';
import { MatSelectModule } from '@angular/material/select';

@NgModule({
  exports: [
    FlexLayoutModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatCardModule,
    MatCarouselModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatDayjsDateModule,
    MatDividerModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPasswordStrengthModule,
    MatProgressSpinnerModule,
    MatRippleModule,
    MatSelectModule,
    MatStepperModule,
  ],
})
export class MaterialModule {}
