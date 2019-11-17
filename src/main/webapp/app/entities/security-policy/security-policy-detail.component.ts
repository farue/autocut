import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISecurityPolicy } from 'app/shared/model/security-policy.model';

@Component({
  selector: 'jhi-security-policy-detail',
  templateUrl: './security-policy-detail.component.html'
})
export class SecurityPolicyDetailComponent implements OnInit {
  securityPolicy: ISecurityPolicy;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ securityPolicy }) => {
      this.securityPolicy = securityPolicy;
    });
  }

  previousState() {
    window.history.back();
  }
}
