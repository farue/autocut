import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { SecurityPolicyService } from '../service/security-policy.service';

import { SecurityPolicyComponent } from './security-policy.component';

describe('Component Tests', () => {
  describe('SecurityPolicy Management Component', () => {
    let comp: SecurityPolicyComponent;
    let fixture: ComponentFixture<SecurityPolicyComponent>;
    let service: SecurityPolicyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [SecurityPolicyComponent],
      })
        .overrideTemplate(SecurityPolicyComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SecurityPolicyComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(SecurityPolicyService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
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
      expect(comp.securityPolicies?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
