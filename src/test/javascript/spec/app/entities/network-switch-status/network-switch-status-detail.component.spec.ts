import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchStatusDetailComponent } from 'app/entities/network-switch-status/network-switch-status-detail.component';
import { NetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';

describe('Component Tests', () => {
  describe('NetworkSwitchStatus Management Detail Component', () => {
    let comp: NetworkSwitchStatusDetailComponent;
    let fixture: ComponentFixture<NetworkSwitchStatusDetailComponent>;
    const route = ({ data: of({ networkSwitchStatus: new NetworkSwitchStatus(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchStatusDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
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
        expect(comp.networkSwitchStatus).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
