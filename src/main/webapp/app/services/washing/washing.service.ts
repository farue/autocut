import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LaundryMachine } from 'app/entities/laundry-machine/laundry-machine.model';
import { SERVER_API_URL } from 'app/app.constants';
import { LaundryMachineProgram } from 'app/entities/laundry-machine-program/laundry-machine-program.model';
import { Injectable } from '@angular/core';
import * as dayjs from 'dayjs';
import { tap } from 'rxjs/operators';

export interface ActivateResponse {
  machineId: number;
  activationTimestamp: dayjs.Dayjs;
  endActivationTime: dayjs.Dayjs;
  activationDurationMs: number;
}

@Injectable({ providedIn: 'root' })
export class WashingService {
  public resourceUrl = SERVER_API_URL + 'api/laundry-machines';

  constructor(private http: HttpClient) {}

  getAllLaundryMachines(): Observable<LaundryMachine[]> {
    return this.http.get<LaundryMachine[]>(this.resourceUrl);
  }

  unlock(laundryMachine: LaundryMachine, laundryMachineProgram: LaundryMachineProgram): Observable<ActivateResponse> {
    const machineId = laundryMachine.id!;
    const programId = laundryMachineProgram.id!;
    const params = { programId: String(programId) };
    return this.http.post<ActivateResponse>(`${this.resourceUrl}/${machineId}/unlock`, {}, { params }).pipe(
      tap(response => {
        response.activationTimestamp = dayjs(response.activationTimestamp);
        response.endActivationTime = dayjs(response.endActivationTime);
      })
    );
  }
}
