export interface IAddress {
  id?: number;
  street?: string;
  streetNumber?: string;
  zip?: string;
  city?: string;
  country?: string;
}

export class Address implements IAddress {
  constructor(
    public id?: number,
    public street?: string,
    public streetNumber?: string,
    public zip?: string,
    public city?: string,
    public country?: string
  ) {}
}

export function getAddressIdentifier(address: IAddress): number | undefined {
  return address.id;
}
