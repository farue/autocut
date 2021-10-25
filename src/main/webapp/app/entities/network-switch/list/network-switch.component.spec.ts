import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { NetworkSwitchService } from '../service/network-switch.service';

import { NetworkSwitchComponent } from './network-switch.component';

describe('NetworkSwitch Management Component', () => {
  let comp: NetworkSwitchComponent;
  let fixture: ComponentFixture<NetworkSwitchComponent>;
  let service: NetworkSwitchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [NetworkSwitchComponent],
    })
      .overrideTemplate(NetworkSwitchComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NetworkSwitchComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NetworkSwitchService);

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
    expect(comp.networkSwitches?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
