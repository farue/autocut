import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { TotpComponent } from 'app/entities/totp/totp.component';
import { TotpService } from 'app/entities/totp/totp.service';
import { Totp } from 'app/shared/model/totp.model';

describe('Component Tests', () => {
  describe('Totp Management Component', () => {
    let comp: TotpComponent;
    let fixture: ComponentFixture<TotpComponent>;
    let service: TotpService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TotpComponent],
        providers: []
      })
        .overrideTemplate(TotpComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TotpComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TotpService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Totp(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.totps && comp.totps[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
