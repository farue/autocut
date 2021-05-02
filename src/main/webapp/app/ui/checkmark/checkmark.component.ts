import { Component, Input } from '@angular/core';

@Component({
  selector: 'jhi-checkmark',
  templateUrl: './checkmark.component.html',
  styleUrls: ['./checkmark.component.scss'],
})
export class CheckmarkComponent {
  @Input()
  visible = true;
}
