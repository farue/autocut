import { Component, OnInit } from '@angular/core';
import { EMPTY, of, timer } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { PublicService } from 'app/shared/service/public.service';
import { BroadcastMessage } from 'app/entities/broadcast-message/broadcast-message.model';
import { AccountService } from 'app/core/auth/account.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'jhi-broadcast-messages',
  templateUrl: './broadcast-messages.component.html',
  styleUrls: ['./broadcast-messages.component.scss'],
})
export class BroadcastMessagesComponent implements OnInit {
  broadcastMessages: BroadcastMessage[] = [];

  constructor(private publicService: PublicService, private accountService: AccountService, private translateService: TranslateService) {}

  ngOnInit(): void {
    this.accountService.getAuthenticationState().subscribe(() => this.loadBroadcastMessages());
    timer(0, 300000)
      .pipe(
        switchMap(v =>
          of(v).pipe(
            // create inner observable to continue outer observable on errors
            tap(() => this.loadBroadcastMessages()),
            catchError(() => EMPTY)
          )
        )
      )
      .subscribe();
  }

  getBroadcastMessageText(message: BroadcastMessage): string {
    if (message.messageTexts?.length) {
      for (const text of message.messageTexts) {
        if (text.langKey === this.translateService.currentLang) {
          return text.text!;
        }
      }
      return message.messageTexts[0].text!;
    }
    return '';
  }

  private loadBroadcastMessages(): void {
    this.publicService.getBroadcastMessages().subscribe(messages => (this.broadcastMessages = messages));
  }
}
