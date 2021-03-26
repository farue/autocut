import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LaundryMachineProgramDetailComponent } from './laundry-machine-program-detail.component';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Detail Component', () => {
    let comp: LaundryMachineProgramDetailComponent;
    let fixture: ComponentFixture<LaundryMachineProgramDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [LaundryMachineProgramDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ laundryMachineProgram: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(LaundryMachineProgramDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LaundryMachineProgramDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load laundryMachineProgram on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.laundryMachineProgram).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
