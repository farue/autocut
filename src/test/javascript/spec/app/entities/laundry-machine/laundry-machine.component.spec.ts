import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { LaundryMachineComponent } from 'app/entities/laundry-machine/laundry-machine.component';
import { LaundryMachineService } from 'app/entities/laundry-machine/laundry-machine.service';
import { LaundryMachine } from 'app/shared/model/laundry-machine.model';

describe('Component Tests', () => {
  describe('LaundryMachine Management Component', () => {
    let comp: LaundryMachineComponent;
    let fixture: ComponentFixture<LaundryMachineComponent>;
    let service: LaundryMachineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineComponent]
      })
        .overrideTemplate(LaundryMachineComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryMachineComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(LaundryMachineService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new LaundryMachine(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.laundryMachines && comp.laundryMachines[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
