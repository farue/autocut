import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BroadcastMessageTextDetailComponent } from './broadcast-message-text-detail.component';

describe('Component Tests', () => {
  describe('BroadcastMessageText Management Detail Component', () => {
    let comp: BroadcastMessageTextDetailComponent;
    let fixture: ComponentFixture<BroadcastMessageTextDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [BroadcastMessageTextDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ broadcastMessageText: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(BroadcastMessageTextDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BroadcastMessageTextDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load broadcastMessageText on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.broadcastMessageText).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
