import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { InternetAccessDetailComponent } from './internet-access-detail.component';

describe('Component Tests', () => {
  describe('InternetAccess Management Detail Component', () => {
    let comp: InternetAccessDetailComponent;
    let fixture: ComponentFixture<InternetAccessDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [InternetAccessDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ internetAccess: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(InternetAccessDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(InternetAccessDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load internetAccess on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.internetAccess).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
