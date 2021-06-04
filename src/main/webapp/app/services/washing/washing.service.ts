import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from 'app/app.constants';
import { Injectable } from '@angular/core';
import * as dayjs from 'dayjs';
import { tap } from 'rxjs/operators';
import { toDate } from 'app/core/util/date-util';
import { Machine, Program } from 'app/entities/washing/washing.model';

export interface ActivateResponse {
  machineId: number;
  activationTimestamp: dayjs.Dayjs;
  endActivationTime: dayjs.Dayjs;
  activationDurationMs: number;
}

@Injectable({ providedIn: 'root' })
export class WashingService {
  public resourceUrl = SERVER_API_URL + 'api/washing/laundry-machines';

  constructor(private http: HttpClient) {}

  getAllMachines(): Observable<Machine[]> {
    return this.http
      .get<Machine[]>(this.resourceUrl)
      .pipe(tap((machines: Machine[]) => machines.forEach(machine => (machine.inUseUntil = toDate(machine.inUseUntil)))));
  }

  getPrograms(machine: Machine): Observable<Program[]> {
    return this.http.get<Program[]>(`${this.resourceUrl}/${machine.id}/programs`);
  }

  unlock(machine: Machine, program: Program): Observable<ActivateResponse> {
    const machineId = machine.id;
    const programId = program.id;
    const params = { programId: String(programId) };
    return this.http.post<ActivateResponse>(`${this.resourceUrl}/${machineId}/unlock`, {}, { params }).pipe(
      tap(response => {
        response.activationTimestamp = dayjs(response.activationTimestamp);
        response.endActivationTime = dayjs(response.endActivationTime);
      })
    );
  }
}
