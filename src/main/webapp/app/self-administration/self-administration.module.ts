import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import { SelfAdministrationComponent } from "./self-administration.component";
import { SelfAdministrationRoutingModule } from "app/self-administration/self-administration-routing.module";

@NgModule({
  imports: [SharedModule, SelfAdministrationRoutingModule],
  declarations: [SelfAdministrationComponent],
})
export class SelfAdministrationModule {}
