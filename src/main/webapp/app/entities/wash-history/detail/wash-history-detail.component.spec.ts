import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';

import {WashHistoryDetailComponent} from './wash-history-detail.component';

describe('Component Tests', () => {
  describe('WashHistory Management Detail Component', () => {
    let comp: WashHistoryDetailComponent;
    let fixture: ComponentFixture<WashHistoryDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [WashHistoryDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ washHistory: { id: 123 } }) },
          },
        ],
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
        expect(comp.washHistory).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
