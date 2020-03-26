import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ILaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';

type EntityResponseType = HttpResponse<ILaundryMachineProgram>;
type EntityArrayResponseType = HttpResponse<ILaundryMachineProgram[]>;

@Injectable({ providedIn: 'root' })
export class LaundryMachineProgramService {
  public resourceUrl = SERVER_API_URL + 'api/laundry-machine-programs';

  constructor(protected http: HttpClient) {}

  create(laundryMachineProgram: ILaundryMachineProgram): Observable<EntityResponseType> {
    return this.http.post<ILaundryMachineProgram>(this.resourceUrl, laundryMachineProgram, { observe: 'response' });
  }

  update(laundryMachineProgram: ILaundryMachineProgram): Observable<EntityResponseType> {
    return this.http.put<ILaundryMachineProgram>(this.resourceUrl, laundryMachineProgram, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILaundryMachineProgram>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILaundryMachineProgram[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
