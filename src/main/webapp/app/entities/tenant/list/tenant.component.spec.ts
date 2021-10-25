import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TenantService } from '../service/tenant.service';

import { TenantComponent } from './tenant.component';

describe('Tenant Management Component', () => {
  let comp: TenantComponent;
  let fixture: ComponentFixture<TenantComponent>;
  let service: TenantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TenantComponent],
    })
      .overrideTemplate(TenantComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TenantComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TenantService);

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
    expect(comp.tenants?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
