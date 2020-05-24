import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { GlobalSettingDetailComponent } from 'app/entities/global-setting/global-setting-detail.component';
import { GlobalSetting } from 'app/shared/model/global-setting.model';

describe('Component Tests', () => {
  describe('GlobalSetting Management Detail Component', () => {
    let comp: GlobalSettingDetailComponent;
    let fixture: ComponentFixture<GlobalSettingDetailComponent>;
    const route = ({ data: of({ globalSetting: new GlobalSetting(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [GlobalSettingDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(GlobalSettingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GlobalSettingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load globalSetting on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.globalSetting).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
