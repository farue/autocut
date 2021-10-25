import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { LeaseService } from '../service/lease.service';

import { LeaseComponent } from './lease.component';

describe('Component Tests', () => {
  describe('Lease Management Component', () => {
    let comp: LeaseComponent;
    let fixture: ComponentFixture<LeaseComponent>;
    let service: LeaseService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LeaseComponent],
      })
        .overrideTemplate(LeaseComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LeaseComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(LeaseService);

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
      expect(comp.leases?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
