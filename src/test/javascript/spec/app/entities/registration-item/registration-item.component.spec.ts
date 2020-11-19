import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { RegistrationItemComponent } from 'app/entities/registration-item/registration-item.component';
import { RegistrationItemService } from 'app/entities/registration-item/registration-item.service';
import { RegistrationItem } from 'app/shared/model/registration-item.model';

describe('Component Tests', () => {
  describe('RegistrationItem Management Component', () => {
    let comp: RegistrationItemComponent;
    let fixture: ComponentFixture<RegistrationItemComponent>;
    let service: RegistrationItemService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [RegistrationItemComponent],
      })
        .overrideTemplate(RegistrationItemComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RegistrationItemComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RegistrationItemService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new RegistrationItem(123)],
            headers,
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.registrationItems && comp.registrationItems[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
