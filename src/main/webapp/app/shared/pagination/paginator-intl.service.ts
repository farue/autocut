import { TranslateService } from '@ngx-translate/core';
import { MatPaginatorIntl } from '@angular/material/paginator';

export class PaginatorIntlService extends MatPaginatorIntl {
  itemsPerPageLabel = 'Items per page';
  nextPageLabel = 'Next page';
  previousPageLabel = 'Previous page';

  constructor(private translate: TranslateService) {
    super();

    this.translate.onLangChange.subscribe(() => {
      this.translateLabels();
    });

    this.translateLabels();
  }

  getRangeLabel = (page: number, pageSize: number, length: number): string => {
    const of: string = this.translate.instant('global.pagination.of');
    if (length === 0 || pageSize === 0) {
      return `0 ${of} ${String(length)}`;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    // If the start index exceeds the list length, do not try and fix the end index to the end.
    const endIndex = startIndex < length ? Math.min(startIndex + pageSize, length) : startIndex + pageSize;
    return `${startIndex + 1} - ${endIndex} ${of} ${length}`;
  };

  translateLabels(): void {
    this.itemsPerPageLabel = this.translate.instant('global.pagination.items_per_page');
    this.nextPageLabel = this.translate.instant('global.pagination.next_page');
    this.previousPageLabel = this.translate.instant('global.pagination.previous_page');
  }
}
