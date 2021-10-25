import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LaundryMachineDetailComponent } from './laundry-machine-detail.component';

describe('LaundryMachine Management Detail Component', () => {
  let comp: LaundryMachineDetailComponent;
  let fixture: ComponentFixture<LaundryMachineDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LaundryMachineDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ laundryMachine: { id: 123 } }) },
        },
      ],
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
      expect(comp.laundryMachine).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
