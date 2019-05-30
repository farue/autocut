import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { AutocutSharedLibsModule, AutocutSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [AutocutSharedLibsModule, AutocutSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [AutocutSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AutocutSharedModule {
  static forRoot() {
    return {
      ngModule: AutocutSharedModule
    };
  }
}
