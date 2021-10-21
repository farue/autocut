import {ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpHeaders, HttpResponse} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {of} from 'rxjs';

import {LaundryProgramService} from '../service/laundry-program.service';

import {LaundryProgramComponent} from './laundry-program.component';

describe('Component Tests', () => {
  describe('LaundryProgram Management Component', () => {
    let comp: LaundryProgramComponent;
    let fixture: ComponentFixture<LaundryProgramComponent>;
    let service: LaundryProgramService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LaundryProgramComponent],
      })
        .overrideTemplate(LaundryProgramComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LaundryProgramComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(LaundryProgramService);

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
      expect(comp.laundryPrograms?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
