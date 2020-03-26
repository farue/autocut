import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TotpUpdateComponent } from 'app/entities/totp/totp-update.component';
import { TotpService } from 'app/entities/totp/totp.service';
import { Totp } from 'app/shared/model/totp.model';

describe('Component Tests', () => {
  describe('Totp Management Update Component', () => {
    let comp: TotpUpdateComponent;
    let fixture: ComponentFixture<TotpUpdateComponent>;
    let service: TotpService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TotpUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(TotpUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TotpUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TotpService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Totp(123);
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
        const entity = new Totp();
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
