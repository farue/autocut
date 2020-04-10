import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { TeamMemberComponent } from 'app/entities/team-member/team-member.component';
import { TeamMemberService } from 'app/entities/team-member/team-member.service';
import { TeamMember } from 'app/shared/model/team-member.model';

describe('Component Tests', () => {
  describe('TeamMember Management Component', () => {
    let comp: TeamMemberComponent;
    let fixture: ComponentFixture<TeamMemberComponent>;
    let service: TeamMemberService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMemberComponent]
      })
        .overrideTemplate(TeamMemberComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TeamMemberComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TeamMemberService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new TeamMember(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.teamMembers && comp.teamMembers[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
