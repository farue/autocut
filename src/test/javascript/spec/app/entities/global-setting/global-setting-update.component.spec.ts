import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { GlobalSettingUpdateComponent } from 'app/entities/global-setting/global-setting-update.component';
import { GlobalSettingService } from 'app/entities/global-setting/global-setting.service';
import { GlobalSetting } from 'app/shared/model/global-setting.model';

describe('Component Tests', () => {
  describe('GlobalSetting Management Update Component', () => {
    let comp: GlobalSettingUpdateComponent;
    let fixture: ComponentFixture<GlobalSettingUpdateComponent>;
    let service: GlobalSettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [GlobalSettingUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(GlobalSettingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GlobalSettingUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GlobalSettingService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new GlobalSetting(123);
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
        const entity = new GlobalSetting();
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
