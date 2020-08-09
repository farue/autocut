import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TenantCommunicationUpdateComponent } from 'app/entities/tenant-communication/tenant-communication-update.component';
import { TenantCommunicationService } from 'app/entities/tenant-communication/tenant-communication.service';
import { TenantCommunication } from 'app/shared/model/tenant-communication.model';

describe('Component Tests', () => {
  describe('TenantCommunication Management Update Component', () => {
    let comp: TenantCommunicationUpdateComponent;
    let fixture: ComponentFixture<TenantCommunicationUpdateComponent>;
    let service: TenantCommunicationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantCommunicationUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(TenantCommunicationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantCommunicationUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TenantCommunicationService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new TenantCommunication(123);
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
        const entity = new TenantCommunication();
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
