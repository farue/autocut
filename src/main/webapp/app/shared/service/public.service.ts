import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BroadcastMessage } from 'app/entities/broadcast-message/broadcast-message.model';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from '../../core/config/application-config.service';

@Injectable({ providedIn: 'root' })
export class PublicService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/public');

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  getBroadcastMessages(): Observable<BroadcastMessage[]> {
    return this.http.get<BroadcastMessage[]>(`${this.resourceUrl}/broadcast-messages`);
  }
}
