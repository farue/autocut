import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { LaundryMachineProgramDetailComponent } from 'app/entities/laundry-machine-program/laundry-machine-program-detail.component';
import { LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

describe('Component Tests', () => {
  describe('LaundryMachineProgram Management Detail Component', () => {
    let comp: LaundryMachineProgramDetailComponent;
    let fixture: ComponentFixture<LaundryMachineProgramDetailComponent>;
    const route = ({ data: of({ laundryMachineProgram: new LaundryMachineProgram(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineProgramDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
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
