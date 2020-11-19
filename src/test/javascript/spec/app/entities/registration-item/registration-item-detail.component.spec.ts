import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { RegistrationItemDetailComponent } from 'app/entities/registration-item/registration-item-detail.component';
import { RegistrationItem } from 'app/shared/model/registration-item.model';

describe('Component Tests', () => {
  describe('RegistrationItem Management Detail Component', () => {
    let comp: RegistrationItemDetailComponent;
    let fixture: ComponentFixture<RegistrationItemDetailComponent>;
    const route = ({ data: of({ registrationItem: new RegistrationItem(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [RegistrationItemDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(RegistrationItemDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RegistrationItemDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load registrationItem on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.registrationItem).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
