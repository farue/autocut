import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TeamMemberDetailComponent } from 'app/entities/team-member/team-member-detail.component';
import { TeamMember } from 'app/shared/model/team-member.model';

describe('Component Tests', () => {
  describe('TeamMember Management Detail Component', () => {
    let comp: TeamMemberDetailComponent;
    let fixture: ComponentFixture<TeamMemberDetailComponent>;
    const route = ({ data: of({ teamMember: new TeamMember(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMemberDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(TeamMemberDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TeamMemberDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load teamMember on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.teamMember).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
