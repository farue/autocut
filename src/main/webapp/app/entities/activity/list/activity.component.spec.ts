import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ActivityService } from '../service/activity.service';

import { ActivityComponent } from './activity.component';

describe('Activity Management Component', () => {
  let comp: ActivityComponent;
  let fixture: ComponentFixture<ActivityComponent>;
  let service: ActivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ActivityComponent],
    })
      .overrideTemplate(ActivityComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ActivityComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ActivityService);

    const headers = new HttpHeaders();
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
    expect(comp.activities?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
