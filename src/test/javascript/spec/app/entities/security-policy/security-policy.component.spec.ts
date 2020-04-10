import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { SecurityPolicyComponent } from 'app/entities/security-policy/security-policy.component';
import { SecurityPolicyService } from 'app/entities/security-policy/security-policy.service';
import { SecurityPolicy } from 'app/shared/model/security-policy.model';

describe('Component Tests', () => {
  describe('SecurityPolicy Management Component', () => {
    let comp: SecurityPolicyComponent;
    let fixture: ComponentFixture<SecurityPolicyComponent>;
    let service: SecurityPolicyService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [SecurityPolicyComponent]
      })
        .overrideTemplate(SecurityPolicyComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(SecurityPolicyComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SecurityPolicyService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new SecurityPolicy(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.securityPolicies && comp.securityPolicies[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
