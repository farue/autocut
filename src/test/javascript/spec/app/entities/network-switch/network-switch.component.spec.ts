import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchComponent } from 'app/entities/network-switch/network-switch.component';
import { NetworkSwitchService } from 'app/entities/network-switch/network-switch.service';
import { NetworkSwitch } from 'app/shared/model/network-switch.model';

describe('Component Tests', () => {
  describe('NetworkSwitch Management Component', () => {
    let comp: NetworkSwitchComponent;
    let fixture: ComponentFixture<NetworkSwitchComponent>;
    let service: NetworkSwitchService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchComponent],
        providers: []
      })
        .overrideTemplate(NetworkSwitchComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NetworkSwitchComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NetworkSwitchService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new NetworkSwitch(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.networkSwitches[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
