import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ApartmentDetailComponent } from './apartment-detail.component';

describe('Apartment Management Detail Component', () => {
  let comp: ApartmentDetailComponent;
  let fixture: ComponentFixture<ApartmentDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ApartmentDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ apartment: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ApartmentDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ApartmentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load apartment on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.apartment).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
