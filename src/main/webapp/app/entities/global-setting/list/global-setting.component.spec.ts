import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { GlobalSettingService } from '../service/global-setting.service';

import { GlobalSettingComponent } from './global-setting.component';

describe('Component Tests', () => {
  describe('GlobalSetting Management Component', () => {
    let comp: GlobalSettingComponent;
    let fixture: ComponentFixture<GlobalSettingComponent>;
    let service: GlobalSettingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [GlobalSettingComponent],
      })
        .overrideTemplate(GlobalSettingComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(GlobalSettingComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(GlobalSettingService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.globalSettings?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
