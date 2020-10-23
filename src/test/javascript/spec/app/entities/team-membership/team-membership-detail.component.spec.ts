import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TeamMembershipDetailComponent } from 'app/entities/team-membership/team-membership-detail.component';
import { TeamMembership } from 'app/shared/model/team-membership.model';

describe('Component Tests', () => {
  describe('TeamMembership Management Detail Component', () => {
    let comp: TeamMembershipDetailComponent;
    let fixture: ComponentFixture<TeamMembershipDetailComponent>;
    const route = ({ data: of({ teamMembership: new TeamMembership(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMembershipDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(TeamMembershipDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TeamMembershipDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load teamMembership on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.teamMembership).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
