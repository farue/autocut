import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TeamMemberUpdateComponent } from 'app/entities/team-member/team-member-update.component';
import { TeamMemberService } from 'app/entities/team-member/team-member.service';
import { TeamMember } from 'app/shared/model/team-member.model';

describe('Component Tests', () => {
  describe('TeamMember Management Update Component', () => {
    let comp: TeamMemberUpdateComponent;
    let fixture: ComponentFixture<TeamMemberUpdateComponent>;
    let service: TeamMemberService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMemberUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(TeamMemberUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TeamMemberUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TeamMemberService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new TeamMember(123);
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
        const entity = new TeamMember();
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
