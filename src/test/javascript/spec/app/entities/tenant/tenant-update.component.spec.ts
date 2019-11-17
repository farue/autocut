import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TenantUpdateComponent } from 'app/entities/tenant/tenant-update.component';
import { TenantService } from 'app/entities/tenant/tenant.service';
import { Tenant } from 'app/shared/model/tenant.model';

describe('Component Tests', () => {
  describe('Tenant Management Update Component', () => {
    let comp: TenantUpdateComponent;
    let fixture: ComponentFixture<TenantUpdateComponent>;
    let service: TenantService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(TenantUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TenantService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Tenant(123);
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
        const entity = new Tenant();
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
