import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { Pagination } from 'app/core/request/request.model';
import { getUserIdentifier, IUser } from './user.model';
import { catchError, map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class UserService {
  private resourceUrl = this.applicationConfigService.getEndpointFor('api/users');

  constructor(private http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  query(req?: Pagination): Observable<HttpResponse<IUser[]>> {
    const options = createRequestOption(req);
    return this.http.get<IUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  addUserToCollectionIfMissing(userCollection: IUser[], ...usersToCheck: (IUser | null | undefined)[]): IUser[] {
    const users: IUser[] = usersToCheck.filter(isPresent);
    if (users.length > 0) {
      const userCollectionIdentifiers = userCollection.map(userItem => getUserIdentifier(userItem)!);
      const usersToAdd = users.filter(userItem => {
        const userIdentifier = getUserIdentifier(userItem);
        if (userIdentifier == null || userCollectionIdentifiers.includes(userIdentifier)) {
          return false;
        }
        userCollectionIdentifiers.push(userIdentifier);
        return true;
      });
      return [...usersToAdd, ...userCollection];
    }
    return userCollection;
  }

  usernameExists(username: string): Observable<boolean> {
    return this.http.get<{}>(`${this.resourceUrl}/username=${username}`).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => {
        if (err.status === 404) {
          return of(false);
        }
        throw new Error('Unexpected server error during username validation.');
      })
    );
  }

  emailExists(email: string): Observable<boolean> {
    return this.http.get<{}>(`${this.resourceUrl}/email=${email}`).pipe(
      map(() => true),
      catchError((err: HttpErrorResponse) => {
        if (err.status === 404) {
          return of(false);
        }
        throw new Error('Unexpected server error during email validation.');
      })
    );
  }
}
