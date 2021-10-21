import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';

import {BroadcastMessageService} from '../service/broadcast-message.service';

import {BroadcastMessageComponent} from './broadcast-message.component';

describe('Component Tests', () => {
  describe('BroadcastMessage Management Component', () => {
    let comp: BroadcastMessageComponent;
    let fixture: ComponentFixture<BroadcastMessageComponent>;
    let service: BroadcastMessageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BroadcastMessageComponent],
      })
        .overrideTemplate(BroadcastMessageComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BroadcastMessageComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BroadcastMessageService);

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
      expect(comp.broadcastMessages?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
