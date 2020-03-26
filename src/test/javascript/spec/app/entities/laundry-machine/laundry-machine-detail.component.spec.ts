import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { LaundryMachineDetailComponent } from 'app/entities/laundry-machine/laundry-machine-detail.component';
import { LaundryMachine } from 'app/shared/model/laundry-machine.model';

describe('Component Tests', () => {
  describe('LaundryMachine Management Detail Component', () => {
    let comp: LaundryMachineDetailComponent;
    let fixture: ComponentFixture<LaundryMachineDetailComponent>;
    const route = ({ data: of({ laundryMachine: new LaundryMachine(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [LaundryMachineDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(LaundryMachineDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LaundryMachineDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load laundryMachine on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.laundryMachine).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
