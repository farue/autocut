import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { LaundryMachineProgramComponent } from 'app/entities/laundry-machine-program/laundry-machine-program.component';
import { LaundryMachineProgramService } from 'app/entities/laundry-machine-program/laundry-machine-program.service';
import { LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Component', () => {
    let comp: LaundryMachineProgramComponent;
    let fixture: ComponentFixture<LaundryMachineProgramComponent>;
    let service: LaundryMachineProgramService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineProgramComponent]
      })
        .overrideTemplate(LaundryMachineProgramComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryMachineProgramComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LaundryMachineProgramService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new LaundryMachineProgram(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.laundryMachinePrograms && comp.laundryMachinePrograms[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
