import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { InternetAccessUpdateComponent } from 'app/entities/internet-access/internet-access-update.component';
import { InternetAccessService } from 'app/entities/internet-access/internet-access.service';
import { InternetAccess } from 'app/shared/model/internet-access.model';

describe('Component Tests', () => {
  describe('InternetAccess Management Update Component', () => {
    let comp: InternetAccessUpdateComponent;
    let fixture: ComponentFixture<InternetAccessUpdateComponent>;
    let service: InternetAccessService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternetAccessUpdateComponent],
        providers: [FormBuilder],
      })
        .overrideTemplate(InternetAccessUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(InternetAccessUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(InternetAccessService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new InternetAccess(123);
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
        const entity = new InternetAccess();
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
