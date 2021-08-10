import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BroadcastMessage } from 'app/entities/broadcast-message/broadcast-message.model';
import { SERVER_API_URL } from 'app/app.constants';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PublicService {
  public resourceUrl = SERVER_API_URL + 'api/public';

  constructor(private http: HttpClient) {}

  getBroadcastMessages(): Observable<BroadcastMessage[]> {
    return this.http.get<BroadcastMessage[]>(`${this.resourceUrl}/broadcast-messages`);
  }
}
