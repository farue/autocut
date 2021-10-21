import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {SecurityPolicyDetailComponent} from './security-policy-detail.component';

describe('Component Tests', () => {
  describe('SecurityPolicy Management Detail Component', () => {
    let comp: SecurityPolicyDetailComponent;
    let fixture: ComponentFixture<SecurityPolicyDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SecurityPolicyDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ securityPolicy: { id: 123 } }) },
          },
        ],
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
        expect(comp.securityPolicy).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
