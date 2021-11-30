import { Component } from '@angular/core';

export interface Contact {
  name: string;
  email: string;
  phone: string;
  mobile: string;
}

@Component({
  selector: 'jhi-contact-janitor',
  templateUrl: './contact-janitor.component.html',
  styleUrls: ['./contact-janitor.component.scss'],
})
export class ContactJanitorComponent {
  janitors: Contact[] = [
    {
      name: 'Uwe Finck',
      email: 'uwe.finck@stw.rwth-aachen.de',
      phone: '0241 89029-522',
      mobile: '0170 763 088 2',
    },
    {
      name: 'Franz-Werner Thoma',
      email: 'franz-werner.thoma@stw.rwth-aachen.de',
      phone: '0241 89029-521',
      mobile: '0160 972 664 35',
    },
  ];

  data: string[][] = Object.keys(this.janitors[0]).map((key: string) => [key].concat(this.janitors.map(j => j[key as keyof Contact])));

  columns = this.data[0].map((_, i) => `j${i}`);
}
