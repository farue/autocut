import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { InternetAccessDetailComponent } from 'app/entities/internet-access/internet-access-detail.component';
import { InternetAccess } from 'app/shared/model/internet-access.model';

describe('Component Tests', () => {
  describe('InternetAccess Management Detail Component', () => {
    let comp: InternetAccessDetailComponent;
    let fixture: ComponentFixture<InternetAccessDetailComponent>;
    const route = ({ data: of({ internetAccess: new InternetAccess(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [InternetAccessDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
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
        expect(comp.internetAccess).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
