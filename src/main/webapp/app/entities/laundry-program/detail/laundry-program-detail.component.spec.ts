import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {LaundryProgramDetailComponent} from './laundry-program-detail.component';

describe('Component Tests', () => {
  describe('LaundryProgram Management Detail Component', () => {
    let comp: LaundryProgramDetailComponent;
    let fixture: ComponentFixture<LaundryProgramDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [LaundryProgramDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ laundryProgram: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(LaundryProgramDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(LaundryProgramDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load laundryProgram on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.laundryProgram).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
