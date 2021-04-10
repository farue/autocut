import { NgModule } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatCarouselModule } from '@ngbmodule/material-carousel';

@NgModule({ exports: [FlexLayoutModule, MatCardModule, MatCarouselModule] })
export class MaterialModule {}
