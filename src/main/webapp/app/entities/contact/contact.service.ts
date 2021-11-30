import { Injectable } from '@angular/core';
import { ContactForm } from './contact.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from '../../core/config/application-config.service';

@Injectable({ providedIn: 'root' })
export class ContactService {
  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  send(team: string, contactForm: ContactForm): Observable<void> {
    const resourceUrl = this.applicationConfigService.getEndpointFor(`api/contact/${team}/email`);
    return this.http.post<void>(resourceUrl, contactForm);
  }
}
