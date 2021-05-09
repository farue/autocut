import { Component, Input } from '@angular/core';
import { IInternalTransaction } from 'app/entities/internal-transaction/internal-transaction.model';
import * as dayjs from 'dayjs';

@Component({
  selector: 'jhi-transaction-cell',
  templateUrl: './transaction-cell.component.html',
  styleUrls: ['./transaction-cell.component.scss'],
})
export class TransactionCellComponent {
  @Input()
  transaction!: IInternalTransaction;

  isFuture(date: dayjs.Dayjs): boolean {
    return date.isAfter(dayjs());
  }

  isEqual(date1: dayjs.Dayjs, date2: dayjs.Dayjs): boolean {
    return date1.isSame(date2, 'day');
  }
}
