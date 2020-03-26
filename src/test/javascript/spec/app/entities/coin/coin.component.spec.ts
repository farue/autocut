import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { AutocutTestModule } from '../../../test.module';
import { CoinComponent } from 'app/entities/coin/coin.component';
import { CoinService } from 'app/entities/coin/coin.service';
import { Coin } from 'app/shared/model/coin.model';

describe('Component Tests', () => {
  describe('Coin Management Component', () => {
    let comp: CoinComponent;
    let fixture: ComponentFixture<CoinComponent>;
    let service: CoinService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [CoinComponent],
        providers: []
      })
        .overrideTemplate(CoinComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(CoinComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(CoinService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Coin(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.coins && comp.coins[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
