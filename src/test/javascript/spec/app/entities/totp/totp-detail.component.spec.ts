import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { TotpDetailComponent } from 'app/entities/totp/totp-detail.component';
import { Totp } from 'app/shared/model/totp.model';

describe('Component Tests', () => {
  describe('Totp Management Detail Component', () => {
    let comp: TotpDetailComponent;
    let fixture: ComponentFixture<TotpDetailComponent>;
    const route = ({ data: of({ totp: new Totp(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TotpDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(TotpDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TotpDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load totp on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.totp).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
