import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { TeamMemberDeleteDialogComponent } from 'app/entities/team-member/team-member-delete-dialog.component';
import { TeamMemberService } from 'app/entities/team-member/team-member.service';

describe('Component Tests', () => {
  describe('TeamMember Management Delete Component', () => {
    let comp: TeamMemberDeleteDialogComponent;
    let fixture: ComponentFixture<TeamMemberDeleteDialogComponent>;
    let service: TeamMemberService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TeamMemberDeleteDialogComponent]
      })
        .overrideTemplate(TeamMemberDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TeamMemberDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TeamMemberService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
