jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { GlobalSettingService } from '../service/global-setting.service';
import { GlobalSetting, IGlobalSetting } from '../global-setting.model';

import { GlobalSettingUpdateComponent } from './global-setting-update.component';

describe('Component Tests', () => {
  describe('GlobalSetting Management Update Component', () => {
    let comp: GlobalSettingUpdateComponent;
    let fixture: ComponentFixture<GlobalSettingUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let globalSettingService: GlobalSettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [GlobalSettingUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(GlobalSettingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GlobalSettingUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      globalSettingService = TestBed.inject(GlobalSettingService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const globalSetting: IGlobalSetting = { id: 456 };

        activatedRoute.data = of({ globalSetting });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(globalSetting));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<GlobalSetting>>();
        const globalSetting = { id: 123 };
        jest.spyOn(globalSettingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ globalSetting });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: globalSetting }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(globalSettingService.update).toHaveBeenCalledWith(globalSetting);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<GlobalSetting>>();
        const globalSetting = new GlobalSetting();
        jest.spyOn(globalSettingService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ globalSetting });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: globalSetting }));
        saveSubject.complete();

        // THEN
        expect(globalSettingService.create).toHaveBeenCalledWith(globalSetting);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<GlobalSetting>>();
        const globalSetting = { id: 123 };
        jest.spyOn(globalSettingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ globalSetting });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(globalSettingService.update).toHaveBeenCalledWith(globalSetting);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
