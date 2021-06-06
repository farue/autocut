import { Injectable } from '@angular/core';
import { MediaChange, MediaObserver } from '@angular/flex-layout';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class MediaService {
  isXs$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'xs')));
  isSm$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'sm')));
  isMd$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'md')));
  isLg$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'lg')));
  isXl$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'xl')));

  isLtSm$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'lt-sm')));
  isLtMd$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'lt-md')));
  isLtLg$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'lt-lg')));
  isLtXl$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'lt-xl')));

  isGtXs$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'gt-xs')));
  isGtSm$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'gt-sm')));
  isGtMd$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'gt-md')));
  isGtLg$ = this.mediaObserver.asObservable().pipe(map((change: MediaChange[]) => this.containsActiveMediaQuery(change, 'gt-lg')));

  constructor(private mediaObserver: MediaObserver) {}

  private containsActiveMediaQuery(change: MediaChange[], mq: string): boolean {
    return change.some((mc: MediaChange) => mc.matches && mc.mqAlias === mq);
  }
}
