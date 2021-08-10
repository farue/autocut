import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BroadcastMessageDetailComponent } from './broadcast-message-detail.component';

describe('Component Tests', () => {
  describe('BroadcastMessage Management Detail Component', () => {
    let comp: BroadcastMessageDetailComponent;
    let fixture: ComponentFixture<BroadcastMessageDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [BroadcastMessageDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ broadcastMessage: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(BroadcastMessageDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BroadcastMessageDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load broadcastMessage on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.broadcastMessage).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
