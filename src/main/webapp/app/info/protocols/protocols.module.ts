import { NgModule } from '@angular/core';
import { AutocutSharedModule } from 'app/shared/shared.module';
import { RouterModule } from '@angular/router';
import { PROTOCOLS_ROUTE } from 'app/info/protocols/protocols.route';
import { ProtocolsComponent } from 'app/info/protocols/protocols.component';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild([PROTOCOLS_ROUTE])],
  declarations: [ProtocolsComponent]
})
export class ProtocolsModule {}
