import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { AutocutSharedModule } from 'app/shared/shared.module';
import { CoinComponent } from './coin.component';
import { CoinDetailComponent } from './coin-detail.component';
import { CoinUpdateComponent } from './coin-update.component';
import { CoinDeleteDialogComponent } from './coin-delete-dialog.component';
import { coinRoute } from './coin.route';

@NgModule({
  imports: [AutocutSharedModule, RouterModule.forChild(coinRoute)],
  declarations: [CoinComponent, CoinDetailComponent, CoinUpdateComponent, CoinDeleteDialogComponent],
  entryComponents: [CoinDeleteDialogComponent]
})
export class AutocutCoinModule {}
