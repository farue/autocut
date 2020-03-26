import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { CommunicationComponent } from 'app/entities/communication/communication.component';
import { CommunicationService } from 'app/entities/communication/communication.service';
import { Communication } from 'app/shared/model/communication.model';

describe('Component Tests', () => {
  describe('Communication Management Component', () => {
    let comp: CommunicationComponent;
    let fixture: ComponentFixture<CommunicationComponent>;
    let service: CommunicationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [CommunicationComponent],
        providers: []
      })
        .overrideTemplate(CommunicationComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CommunicationComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CommunicationService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Communication(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.communications && comp.communications[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
