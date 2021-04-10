import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';

@NgModule({ exports: [FlexLayoutModule, MatCardModule] })
export class MaterialModule {}
