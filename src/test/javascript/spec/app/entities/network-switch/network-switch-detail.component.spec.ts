import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchDetailComponent } from 'app/entities/network-switch/network-switch-detail.component';
import { NetworkSwitch } from 'app/shared/model/network-switch.model';

describe('Component Tests', () => {
  describe('NetworkSwitch Management Detail Component', () => {
    let comp: NetworkSwitchDetailComponent;
    let fixture: ComponentFixture<NetworkSwitchDetailComponent>;
    const route = ({ data: of({ networkSwitch: new NetworkSwitch(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
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
        expect(comp.networkSwitch).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
