import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { SecurityPolicyDetailComponent } from 'app/entities/security-policy/security-policy-detail.component';
import { SecurityPolicy } from 'app/shared/model/security-policy.model';

describe('Component Tests', () => {
  describe('SecurityPolicy Management Detail Component', () => {
    let comp: SecurityPolicyDetailComponent;
    let fixture: ComponentFixture<SecurityPolicyDetailComponent>;
    const route = ({ data: of({ securityPolicy: new SecurityPolicy(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [SecurityPolicyDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(SecurityPolicyDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SecurityPolicyDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load securityPolicy on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.securityPolicy).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
