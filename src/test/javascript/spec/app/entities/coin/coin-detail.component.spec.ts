import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AutocutTestModule } from '../../../test.module';
import { CoinDetailComponent } from 'app/entities/coin/coin-detail.component';
import { Coin } from 'app/shared/model/coin.model';

describe('Component Tests', () => {
  describe('Coin Management Detail Component', () => {
    let comp: CoinDetailComponent;
    let fixture: ComponentFixture<CoinDetailComponent>;
    const route = ({ data: of({ coin: new Coin(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [AutocutTestModule],
        declarations: [CoinDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(CoinDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(CoinDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load coin on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.coin).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
