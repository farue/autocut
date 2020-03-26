import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ILaundryMachine } from 'app/shared/model/laundry-machine.model';

type EntityResponseType = HttpResponse<ILaundryMachine>;
type EntityArrayResponseType = HttpResponse<ILaundryMachine[]>;

@Injectable({ providedIn: 'root' })
export class LaundryMachineService {
  public resourceUrl = SERVER_API_URL + 'api/laundry-machines';

  constructor(protected http: HttpClient) {}

  create(laundryMachine: ILaundryMachine): Observable<EntityResponseType> {
    return this.http.post<ILaundryMachine>(this.resourceUrl, laundryMachine, { observe: 'response' });
  }

  update(laundryMachine: ILaundryMachine): Observable<EntityResponseType> {
    return this.http.put<ILaundryMachine>(this.resourceUrl, laundryMachine, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILaundryMachine>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILaundryMachine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
