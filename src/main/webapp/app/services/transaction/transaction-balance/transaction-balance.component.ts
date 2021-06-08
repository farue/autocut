import { Component, Input } from '@angular/core';

@Component({
  selector: 'jhi-transaction-balance',
  templateUrl: './transaction-balance.component.html',
  styleUrls: ['./transaction-balance.component.scss'],
})
export class TransactionBalanceComponent {
  @Input()
  balance?: number | null;
  @Input()
  label?: string | null;
  @Input()
  typography?: string | null;
  @Input()
  gap?: string | null;
}
