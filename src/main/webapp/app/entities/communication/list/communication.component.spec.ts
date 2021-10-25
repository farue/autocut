import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { CommunicationService } from '../service/communication.service';

import { CommunicationComponent } from './communication.component';

describe('Component Tests', () => {
  describe('Communication Management Component', () => {
    let comp: CommunicationComponent;
    let fixture: ComponentFixture<CommunicationComponent>;
    let service: CommunicationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [CommunicationComponent],
      })
        .overrideTemplate(CommunicationComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CommunicationComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(CommunicationService);

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
      expect(comp.communications?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
