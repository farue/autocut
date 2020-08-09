import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { WashHistoryDetailComponent } from 'app/entities/wash-history/wash-history-detail.component';
import { WashHistory } from 'app/shared/model/wash-history.model';

describe('Component Tests', () => {
  describe('WashHistory Management Detail Component', () => {
    let comp: WashHistoryDetailComponent;
    let fixture: ComponentFixture<WashHistoryDetailComponent>;
    const route = ({ data: of({ washHistory: new WashHistory(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [WashHistoryDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(WashHistoryDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(WashHistoryDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load washHistory on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.washHistory).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
