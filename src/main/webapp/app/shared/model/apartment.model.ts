import { IInternetAccess } from 'app/shared/model/internet-access.model';
import { ILease } from 'app/shared/model/lease.model';
import { IAddress } from 'app/shared/model/address.model';
import { ApartmentTypes } from 'app/shared/model/enumerations/apartment-types.model';

export interface IApartment {
  id?: number;
  apartmentNr?: string;
  apartmentType?: ApartmentTypes;
  maxNumberOfLeases?: number;
  internetAccess?: IInternetAccess;
  leases?: ILease[];
  address?: IAddress;
}

export class Apartment implements IApartment {
  constructor(
    public id?: number,
    public apartmentNr?: string,
    public apartmentType?: ApartmentTypes,
    public maxNumberOfLeases?: number,
    public internetAccess?: IInternetAccess,
    public leases?: ILease[],
    public address?: IAddress
  ) {}
}
