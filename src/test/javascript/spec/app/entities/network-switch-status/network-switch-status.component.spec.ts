import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { NetworkSwitchStatusComponent } from 'app/entities/network-switch-status/network-switch-status.component';
import { NetworkSwitchStatusService } from 'app/entities/network-switch-status/network-switch-status.service';
import { NetworkSwitchStatus } from 'app/shared/model/network-switch-status.model';

describe('Component Tests', () => {
  describe('NetworkSwitchStatus Management Component', () => {
    let comp: NetworkSwitchStatusComponent;
    let fixture: ComponentFixture<NetworkSwitchStatusComponent>;
    let service: NetworkSwitchStatusService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [NetworkSwitchStatusComponent],
      })
        .overrideTemplate(NetworkSwitchStatusComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NetworkSwitchStatusComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NetworkSwitchStatusService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new NetworkSwitchStatus(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.networkSwitchStatuses && comp.networkSwitchStatuses[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
