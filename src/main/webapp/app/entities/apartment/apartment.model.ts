import { IInternetAccess } from 'app/entities/internet-access/internet-access.model';
import { ILease } from 'app/entities/lease/lease.model';
import { IAddress } from 'app/entities/address/address.model';
import { ApartmentTypes } from 'app/entities/enumerations/apartment-types.model';

export interface IApartment {
  id?: number;
  nr?: string;
  type?: ApartmentTypes;
  maxNumberOfLeases?: number;
  internetAccess?: IInternetAccess | null;
  leases?: ILease[] | null;
  address?: IAddress | null;
}

export class Apartment implements IApartment {
  constructor(
    public id?: number,
    public nr?: string,
    public type?: ApartmentTypes,
    public maxNumberOfLeases?: number,
    public internetAccess?: IInternetAccess | null,
    public leases?: ILease[] | null,
    public address?: IAddress | null
  ) {}
}

export function getApartmentIdentifier(apartment: IApartment): number | undefined {
  return apartment.id;
}
