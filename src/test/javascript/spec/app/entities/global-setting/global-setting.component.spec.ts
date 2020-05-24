import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { GlobalSettingComponent } from 'app/entities/global-setting/global-setting.component';
import { GlobalSettingService } from 'app/entities/global-setting/global-setting.service';
import { GlobalSetting } from 'app/shared/model/global-setting.model';

describe('Component Tests', () => {
  describe('GlobalSetting Management Component', () => {
    let comp: GlobalSettingComponent;
    let fixture: ComponentFixture<GlobalSettingComponent>;
    let service: GlobalSettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [GlobalSettingComponent],
      })
        .overrideTemplate(GlobalSettingComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GlobalSettingComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(GlobalSettingService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new GlobalSetting(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.globalSettings && comp.globalSettings[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
