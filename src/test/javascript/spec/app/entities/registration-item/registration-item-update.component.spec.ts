import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { RegistrationItemUpdateComponent } from 'app/entities/registration-item/registration-item-update.component';
import { RegistrationItemService } from 'app/entities/registration-item/registration-item.service';
import { RegistrationItem } from 'app/shared/model/registration-item.model';

describe('Component Tests', () => {
  describe('RegistrationItem Management Update Component', () => {
    let comp: RegistrationItemUpdateComponent;
    let fixture: ComponentFixture<RegistrationItemUpdateComponent>;
    let service: RegistrationItemService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [RegistrationItemUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(RegistrationItemUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RegistrationItemUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RegistrationItemService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new RegistrationItem(123);
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
        const entity = new RegistrationItem();
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
