import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NetworkSwitchDetailComponent } from './network-switch-detail.component';

describe('NetworkSwitch Management Detail Component', () => {
  let comp: NetworkSwitchDetailComponent;
  let fixture: ComponentFixture<NetworkSwitchDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NetworkSwitchDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ networkSwitch: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NetworkSwitchDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NetworkSwitchDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load networkSwitch on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.networkSwitch).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
