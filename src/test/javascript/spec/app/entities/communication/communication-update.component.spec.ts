import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { CommunicationUpdateComponent } from 'app/entities/communication/communication-update.component';
import { CommunicationService } from 'app/entities/communication/communication.service';
import { Communication } from 'app/shared/model/communication.model';

describe('Component Tests', () => {
  describe('Communication Management Update Component', () => {
    let comp: CommunicationUpdateComponent;
    let fixture: ComponentFixture<CommunicationUpdateComponent>;
    let service: CommunicationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [CommunicationUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(CommunicationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CommunicationUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CommunicationService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Communication(123);
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
        const entity = new Communication();
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
