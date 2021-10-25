import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { LaundryMachineService } from '../service/laundry-machine.service';

import { LaundryMachineComponent } from './laundry-machine.component';

describe('LaundryMachine Management Component', () => {
  let comp: LaundryMachineComponent;
  let fixture: ComponentFixture<LaundryMachineComponent>;
  let service: LaundryMachineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LaundryMachineComponent],
    })
      .overrideTemplate(LaundryMachineComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LaundryMachineComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LaundryMachineService);

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
    expect(comp.laundryMachines?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
