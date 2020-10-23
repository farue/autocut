import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { TeamMembershipComponent } from 'app/entities/team-membership/team-membership.component';
import { TeamMembershipService } from 'app/entities/team-membership/team-membership.service';
import { TeamMembership } from 'app/shared/model/team-membership.model';

describe('Component Tests', () => {
  describe('TeamMembership Management Component', () => {
    let comp: TeamMembershipComponent;
    let fixture: ComponentFixture<TeamMembershipComponent>;
    let service: TeamMembershipService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMembershipComponent],
      })
        .overrideTemplate(TeamMembershipComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TeamMembershipComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TeamMembershipService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new TeamMembership(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.teamMemberships && comp.teamMemberships[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
