import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TenantCommunicationService } from '../service/tenant-communication.service';

import { TenantCommunicationComponent } from './tenant-communication.component';

describe('Component Tests', () => {
  describe('TenantCommunication Management Component', () => {
    let comp: TenantCommunicationComponent;
    let fixture: ComponentFixture<TenantCommunicationComponent>;
    let service: TenantCommunicationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TenantCommunicationComponent],
      })
        .overrideTemplate(TenantCommunicationComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TenantCommunicationComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(TenantCommunicationService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.tenantCommunications?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
