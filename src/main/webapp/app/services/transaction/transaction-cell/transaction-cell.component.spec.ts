import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionCellComponent } from './transaction-cell.component';

describe('TransactionCellComponent', () => {
  let component: TransactionCellComponent;
  let fixture: ComponentFixture<TransactionCellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TransactionCellComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionCellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
