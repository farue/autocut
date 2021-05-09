import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'jhi-countdown',
  templateUrl: './countdown.component.html',
  styleUrls: ['./countdown.component.scss'],
})
export class CountdownComponent implements OnChanges {
  @Input()
  seconds?: number;

  @Input()
  size = 100;

  @Output()
  finished: EventEmitter<never> = new EventEmitter<never>();

  secondsLeft = 0;
  progressbarValue = 100;

  private subscription?: Subscription;

  ngOnChanges(changes: SimpleChanges): void {
    if ('seconds' in changes) {
      this.cleanUp();

      const seconds: number = changes.seconds.currentValue;
      this.startNewCountdown(seconds);
    }
  }

  secondsLeftString(): string {
    const minutes = Math.floor(this.secondsLeft / 60);
    const seconds = this.secondsLeft % 60;
    return `${String(minutes)}:${seconds < 10 ? '0' : ''}${seconds}`;
  }

  private startNewCountdown(seconds: number): void {
    if (seconds < 0) {
      throw new Error(`Illegal countdown value: ${String(seconds)}`);
    }

    this.secondsLeft = seconds;

    const timer$ = interval(1000);
    this.subscription = timer$.subscribe(i => {
      this.secondsLeft = seconds - (i + 1);
      this.progressbarValue = (this.secondsLeft * 100) / seconds;
      if (this.secondsLeft === 0) {
        if (this.subscription) {
          this.subscription.unsubscribe();
          this.finished.emit();
        }
      }
    });
  }

  private cleanUp(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
