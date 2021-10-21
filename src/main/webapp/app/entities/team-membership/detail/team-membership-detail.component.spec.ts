import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {TeamMembershipDetailComponent} from './team-membership-detail.component';

describe('Component Tests', () => {
  describe('TeamMembership Management Detail Component', () => {
    let comp: TeamMembershipDetailComponent;
    let fixture: ComponentFixture<TeamMembershipDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TeamMembershipDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ teamMembership: { id: 123 } }) },
          },
        ],
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
        expect(comp.teamMembership).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
