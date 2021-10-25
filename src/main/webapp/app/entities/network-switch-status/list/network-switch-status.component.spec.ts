import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { NetworkSwitchStatusService } from '../service/network-switch-status.service';

import { NetworkSwitchStatusComponent } from './network-switch-status.component';

describe('NetworkSwitchStatus Management Component', () => {
  let comp: NetworkSwitchStatusComponent;
  let fixture: ComponentFixture<NetworkSwitchStatusComponent>;
  let service: NetworkSwitchStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [NetworkSwitchStatusComponent],
    })
      .overrideTemplate(NetworkSwitchStatusComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NetworkSwitchStatusComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NetworkSwitchStatusService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
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
    expect(comp.networkSwitchStatuses?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
