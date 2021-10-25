import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GlobalSettingDetailComponent } from './global-setting-detail.component';

describe('GlobalSetting Management Detail Component', () => {
  let comp: GlobalSettingDetailComponent;
  let fixture: ComponentFixture<GlobalSettingDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GlobalSettingDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ globalSetting: { id: 123 } }) },
        },
      ],
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
      expect(comp.globalSetting).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
