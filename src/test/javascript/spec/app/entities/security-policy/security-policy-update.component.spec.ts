import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { SecurityPolicyUpdateComponent } from 'app/entities/security-policy/security-policy-update.component';
import { SecurityPolicyService } from 'app/entities/security-policy/security-policy.service';
import { SecurityPolicy } from 'app/shared/model/security-policy.model';

describe('Component Tests', () => {
  describe('SecurityPolicy Management Update Component', () => {
    let comp: SecurityPolicyUpdateComponent;
    let fixture: ComponentFixture<SecurityPolicyUpdateComponent>;
    let service: SecurityPolicyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [SecurityPolicyUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(SecurityPolicyUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SecurityPolicyUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SecurityPolicyService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new SecurityPolicy(123);
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
        const entity = new SecurityPolicy();
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
