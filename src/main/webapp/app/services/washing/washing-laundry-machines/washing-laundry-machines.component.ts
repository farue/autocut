import { Component, forwardRef, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import dayjs from 'dayjs/esm';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';
import { range } from 'lodash-es';
import { Machine } from 'app/entities/washing/washing.model';

@Component({
  selector: 'jhi-washing-laundry-machines',
  templateUrl: './washing-laundry-machines.component.html',
  styleUrls: ['./washing-laundry-machines.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => WashingLaundryMachinesComponent),
      multi: true,
    },
  ],
})
export class WashingLaundryMachinesComponent implements OnInit, OnChanges, ControlValueAccessor {
  @Input()
  machines: Machine[] = [];
  @Input()
  reverse = false;
  @Input()
  showLabels = true;

  machinesVM: (Machine | null)[] = [];
  x = 0;
  y = 0;

  buttonGroupControl = new FormControl();

  ngOnInit(): void {
    setInterval(() => {
      // Triggeres change detection
    }, 10000);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ('machines' in changes || 'reverse' in changes) {
      this.updateMachinesVM();
    }
  }

  registerOnChange(fn: any): void {
    this.buttonGroupControl.valueChanges.subscribe(fn);
  }

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  registerOnTouched(): void {}

  writeValue(obj: any): void {
    this.buttonGroupControl.setValue(obj);
  }

  setDisabledState(isDisabled: boolean): void {
    isDisabled ? this.buttonGroupControl.disable() : this.buttonGroupControl.enable();
  }

  timeRemaining(inUseUntil?: dayjs.Dayjs): number | null {
    if (!inUseUntil) {
      return null;
    }
    const timeRemaining = inUseUntil.diff(dayjs(), 'minutes');
    if (timeRemaining < 0) {
      return null;
    }
    return timeRemaining;
  }

  private updateMachinesVM(): void {
    if (this.machines.length === 0) {
      return;
    }

    const machines = [...this.machines].sort((a, b) => a.positionY - b.positionY || a.positionX - b.positionX);

    let minX = Number.MAX_SAFE_INTEGER;
    let minY = Number.MAX_SAFE_INTEGER;
    let maxX = 0;
    let maxY = 0;
    this.machines.forEach(m => {
      if (m.positionX < minX) {
        minX = m.positionX;
      }
      if (m.positionX > maxX) {
        maxX = m.positionX;
      }
      if (m.positionY < minY) {
        minY = m.positionY;
      }
      if (m.positionY > maxY) {
        maxY = m.positionY;
      }
    });
    if (minX > maxX || minY > maxY) {
      return;
    }

    const dx = maxX - minX + 1;
    const dy = maxY - minY + 1;

    const machinesVM: (Machine | null)[] = [];
    let j = this.reverse ? machines.length - 1 : 0;
    for (const y of this.reverse ? range(dy - 1, -1, -1) : range(0, dy, 1)) {
      for (const x of this.reverse ? range(dx - 1, -1, -1) : range(0, dx, 1)) {
        const currentElement = machines.length > j ? machines[j] : undefined;
        if (currentElement && currentElement.positionX === x && currentElement.positionY === y) {
          machinesVM.push(currentElement);
          j = this.reverse ? j - 1 : j + 1;
        } else {
          machinesVM.push(null);
        }
      }
    }

    this.machinesVM = machinesVM;
    this.x = dx;
    this.y = dy;
  }
}
