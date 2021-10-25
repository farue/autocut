import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TeamMembershipService } from '../service/team-membership.service';

import { TeamMembershipComponent } from './team-membership.component';

describe('Component Tests', () => {
  describe('TeamMembership Management Component', () => {
    let comp: TeamMembershipComponent;
    let fixture: ComponentFixture<TeamMembershipComponent>;
    let service: TeamMembershipService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TeamMembershipComponent],
      })
        .overrideTemplate(TeamMembershipComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TeamMembershipComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(TeamMembershipService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.teamMemberships?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
