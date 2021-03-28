import { NgModule } from "@angular/core";
import { SharedModule } from "app/shared/shared.module";
import { AssignmentCommitteeComponent } from "app/self-administration/assignment-committee/assignment-committee.component";
import { RouterModule } from "@angular/router";

@NgModule({
  imports: [SharedModule, RouterModule.forChild([
    {
      path: '',
      component: AssignmentCommitteeComponent,
      pathMatch: 'full',
    }
  ])],
  declarations: [AssignmentCommitteeComponent]
})
export class AssignmentCommitteeModule {}
