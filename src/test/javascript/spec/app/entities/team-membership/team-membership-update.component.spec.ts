import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TeamMembershipUpdateComponent } from 'app/entities/team-membership/team-membership-update.component';
import { TeamMembershipService } from 'app/entities/team-membership/team-membership.service';
import { TeamMembership } from 'app/shared/model/team-membership.model';

describe('Component Tests', () => {
  describe('TeamMembership Management Update Component', () => {
    let comp: TeamMembershipUpdateComponent;
    let fixture: ComponentFixture<TeamMembershipUpdateComponent>;
    let service: TeamMembershipService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMembershipUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(TeamMembershipUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TeamMembershipUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TeamMembershipService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new TeamMembership(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new TeamMembership();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
