import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { ApartmentComponent } from './apartment.component';
import { ApartmentDetailComponent } from './apartment-detail.component';
import { ApartmentUpdateComponent } from './apartment-update.component';
import { ApartmentDeleteDialogComponent, ApartmentDeletePopupComponent } from './apartment-delete-dialog.component';
import { apartmentPopupRoute, apartmentRoute } from './apartment.route';

const ENTITY_STATES = [...apartmentRoute, ...apartmentPopupRoute];

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ApartmentComponent,
    ApartmentDetailComponent,
    ApartmentUpdateComponent,
    ApartmentDeleteDialogComponent,
    ApartmentDeletePopupComponent
  ],
  entryComponents: [ApartmentDeleteDialogComponent]
})
export class AutocutApartmentModule {}
