import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISecurityPolicy } from '../security-policy.model';

@Component({
  selector: 'jhi-security-policy-detail',
  templateUrl: './security-policy-detail.component.html',
})
export class SecurityPolicyDetailComponent implements OnInit {
  securityPolicy: ISecurityPolicy | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ securityPolicy }) => {
      this.securityPolicy = securityPolicy;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
