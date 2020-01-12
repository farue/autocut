import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { TenantCommunicationComponent } from 'app/entities/tenant-communication/tenant-communication.component';
import { TenantCommunicationService } from 'app/entities/tenant-communication/tenant-communication.service';
import { TenantCommunication } from 'app/shared/model/tenant-communication.model';

describe('Component Tests', () => {
  describe('TenantCommunication Management Component', () => {
    let comp: TenantCommunicationComponent;
    let fixture: ComponentFixture<TenantCommunicationComponent>;
    let service: TenantCommunicationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [TenantCommunicationComponent],
        providers: []
      })
        .overrideTemplate(TenantCommunicationComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantCommunicationComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TenantCommunicationService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new TenantCommunication(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.tenantCommunications && comp.tenantCommunications[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
