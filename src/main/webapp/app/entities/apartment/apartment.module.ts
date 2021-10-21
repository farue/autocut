import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {ApartmentComponent} from './list/apartment.component';
import {ApartmentDetailComponent} from './detail/apartment-detail.component';
import {ApartmentUpdateComponent} from './update/apartment-update.component';
import {ApartmentDeleteDialogComponent} from './delete/apartment-delete-dialog.component';
import {ApartmentRoutingModule} from './route/apartment-routing.module';

@NgModule({
  imports: [SharedModule, ApartmentRoutingModule],
  declarations: [ApartmentComponent, ApartmentDetailComponent, ApartmentUpdateComponent, ApartmentDeleteDialogComponent],
  entryComponents: [ApartmentDeleteDialogComponent],
})
export class ApartmentModule {}
