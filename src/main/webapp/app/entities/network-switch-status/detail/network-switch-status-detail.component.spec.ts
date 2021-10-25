import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NetworkSwitchStatusDetailComponent } from './network-switch-status-detail.component';

describe('NetworkSwitchStatus Management Detail Component', () => {
  let comp: NetworkSwitchStatusDetailComponent;
  let fixture: ComponentFixture<NetworkSwitchStatusDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NetworkSwitchStatusDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ networkSwitchStatus: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NetworkSwitchStatusDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NetworkSwitchStatusDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load networkSwitchStatus on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.networkSwitchStatus).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
