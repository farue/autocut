import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchDeleteDialogComponent } from 'app/entities/network-switch/network-switch-delete-dialog.component';
import { NetworkSwitchService } from 'app/entities/network-switch/network-switch.service';

describe('Component Tests', () => {
  describe('NetworkSwitch Management Delete Component', () => {
    let comp: NetworkSwitchDeleteDialogComponent;
    let fixture: ComponentFixture<NetworkSwitchDeleteDialogComponent>;
    let service: NetworkSwitchService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchDeleteDialogComponent]
      })
        .overrideTemplate(NetworkSwitchDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(NetworkSwitchDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NetworkSwitchService);
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
