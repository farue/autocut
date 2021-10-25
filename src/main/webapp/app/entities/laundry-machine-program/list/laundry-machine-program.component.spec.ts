import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { LaundryMachineProgramService } from '../service/laundry-machine-program.service';

import { LaundryMachineProgramComponent } from './laundry-machine-program.component';

describe('LaundryMachineProgram Management Component', () => {
  let comp: LaundryMachineProgramComponent;
  let fixture: ComponentFixture<LaundryMachineProgramComponent>;
  let service: LaundryMachineProgramService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [LaundryMachineProgramComponent],
    })
      .overrideTemplate(LaundryMachineProgramComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LaundryMachineProgramComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LaundryMachineProgramService);

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
    expect(comp.laundryMachinePrograms?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
