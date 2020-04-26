import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LaundryMachine } from 'app/shared/model/laundry-machine.model';
import { SERVER_API_URL } from 'app/app.constants';
import { LaundryMachineProgram } from 'app/shared/model/laundry-machine-program.model';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class WashingService {
  public resourceUrl = SERVER_API_URL + 'api/washing';

  constructor(private http: HttpClient) {}

  getAllLaundryMachines(): Observable<LaundryMachine[]> {
    return this.http.get<LaundryMachine[]>(this.resourceUrl + '/laundry-machines');
  }

  unlock(laundryMachine: LaundryMachine, laundryMachineProgram: LaundryMachineProgram): Observable<{}> {
    const machineId = laundryMachine.id!;
    const programId = laundryMachineProgram.id!;
    const params = { programId: String(programId) };
    return this.http.post(`${this.resourceUrl}/laundry-machines/${machineId}/unlock`, {}, { params });
  }
}
