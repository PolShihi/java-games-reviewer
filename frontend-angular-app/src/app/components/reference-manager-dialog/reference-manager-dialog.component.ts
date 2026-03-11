import { CommonModule } from '@angular/common';
import { Component, DestroyRef, Inject, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { firstValueFrom } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiError } from '../../core/http/api-error';
import { CompanyType } from '../../core/models/company-type';
import { Genre } from '../../core/models/genre';
import { MediaOutlet } from '../../core/models/media-outlet';
import { ProductionCompany } from '../../core/models/production-company';
import { CompanyTypeService } from '../../core/services/company-type.service';
import { GenreService } from '../../core/services/genre.service';
import { MediaOutletService } from '../../core/services/media-outlet.service';
import { ProductionCompanyService } from '../../core/services/production-company.service';
import { fetchAllPageContent } from '../../core/utils/fetch-all-page-content';

export type ReferenceEntityType = 'genres' | 'production-companies' | 'media-outlets';

export interface ReferenceManagerDialogData {
  entityType: ReferenceEntityType;
}

@Component({
  selector: 'app-reference-manager-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    MatTableModule,
  ],
  templateUrl: './reference-manager-dialog.component.html',
  styleUrl: './reference-manager-dialog.component.scss',
})
export class ReferenceManagerDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly genreService = inject(GenreService);
  private readonly companyService = inject(ProductionCompanyService);
  private readonly mediaOutletService = inject(MediaOutletService);
  private readonly companyTypeService = inject(CompanyTypeService);
  private readonly dialogRef = inject(MatDialogRef<ReferenceManagerDialogComponent>);
  private readonly destroyRef = inject(DestroyRef);

  readonly entityType: ReferenceEntityType;

  rows: Array<Genre | ProductionCompany | MediaOutlet> = [];
  companyTypes: CompanyType[] = [];
  loading = false;
  submitting = false;
  error: string | null = null;
  search = '';
  editingId: number | null = null;
  changed = false;

  readonly form = this.fb.group({
    name: this.fb.control<string>('', [Validators.required]),
    websiteUrl: this.fb.control<string>(''),
    foundedYear: this.fb.control<number | null>(null),
    ceo: this.fb.control<string>(''),
    companyTypeId: this.fb.control<number | null>(null),
  });

  constructor(@Inject(MAT_DIALOG_DATA) data: ReferenceManagerDialogData) {
    this.entityType = data.entityType;
  }

  ngOnInit(): void {
    this.dialogRef
      .backdropClick()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.close());
    this.dialogRef
      .keydownEvents()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (event.key === 'Escape') {
          this.close();
        }
      });
    this.loadCompanyTypes();
    this.loadRows();
  }

  get label(): string {
    switch (this.entityType) {
      case 'genres':
        return 'Genres';
      case 'production-companies':
        return 'Production companies';
      case 'media-outlets':
        return 'Media outlets';
      default:
        return 'Reference data';
    }
  }

  get displayedColumns(): string[] {
    if (this.entityType === 'genres') {
      return ['id', 'name', 'actions'];
    }

    if (this.entityType === 'production-companies') {
      return ['id', 'name', 'websiteUrl', 'foundedYear', 'ceo', 'companyType', 'actions'];
    }

    return ['id', 'name', 'websiteUrl', 'foundedYear', 'actions'];
  }

  get filteredRows() {
    const query = this.search.trim().toLowerCase();
    if (!query) {
      return this.rows;
    }

    return this.rows.filter((row) => {
      const name = row.name?.toLowerCase?.() || '';
      const website = (row as MediaOutlet | ProductionCompany).websiteUrl?.toLowerCase?.() || '';
      const ceo = (row as ProductionCompany).ceo?.toLowerCase?.() || '';
      return name.includes(query) || website.includes(query) || ceo.includes(query);
    });
  }

  async loadCompanyTypes() {
    if (this.entityType !== 'production-companies') {
      this.companyTypes = [];
      return;
    }

    try {
      const response = await firstValueFrom(this.companyTypeService.getAll());
      this.companyTypes = Array.isArray(response) ? response : [];
    } catch {
      this.companyTypes = [];
    }
  }

  async loadRows() {
    try {
      this.loading = true;
      this.error = null;

      if (this.entityType === 'genres') {
        const response = await firstValueFrom(this.genreService.getAll());
        this.rows = Array.isArray(response) ? response : [];
        return;
      }

      if (this.entityType === 'production-companies') {
        this.rows = await fetchAllPageContent((params) => this.companyService.getAll(params), {
          sortBy: 'name',
          sortDirection: 'ASC' as const,
        });
        return;
      }

      this.rows = await fetchAllPageContent((params) => this.mediaOutletService.getAll(params), {
        sortBy: 'name',
        sortDirection: 'ASC' as const,
      });
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to load data.';
    } finally {
      this.loading = false;
    }
  }

  resetForm() {
    this.editingId = null;
    this.form.reset({
      name: '',
      websiteUrl: '',
      foundedYear: null,
      ceo: '',
      companyTypeId: null,
    });
  }

  editRow(row: Genre | ProductionCompany | MediaOutlet) {
    this.editingId = row.id ?? null;

    if (this.entityType === 'genres') {
      this.form.patchValue({ name: row.name || '' });
      return;
    }

    if (this.entityType === 'media-outlets') {
      const outlet = row as MediaOutlet;
      this.form.patchValue({
        name: outlet.name || '',
        websiteUrl: outlet.websiteUrl || '',
        foundedYear: outlet.foundedYear ?? null,
      });
      return;
    }

    const company = row as ProductionCompany;
    this.form.patchValue({
      name: company.name || '',
      websiteUrl: company.websiteUrl || '',
      foundedYear: company.foundedYear ?? null,
      ceo: company.ceo || '',
      companyTypeId: (company as ProductionCompany & { companyTypeId?: number }).companyTypeId ?? null,
    });
  }

  private buildPayload() {
    const value = this.form.getRawValue();

    if (this.entityType === 'genres') {
      return { name: value.name?.trim() || '' };
    }

    if (this.entityType === 'media-outlets') {
      return {
        name: value.name?.trim() || '',
        websiteUrl: value.websiteUrl?.trim() || null,
        foundedYear: value.foundedYear ?? null,
      };
    }

    return {
      name: value.name?.trim() || '',
      websiteUrl: value.websiteUrl?.trim() || null,
      foundedYear: value.foundedYear ?? null,
      ceo: value.ceo?.trim() || null,
      companyTypeId: value.companyTypeId ?? null,
    };
  }

  private validateForm(): boolean {
    if (!this.form.value.name?.trim()) {
      this.error = 'Name is required.';
      return false;
    }

    if (this.entityType !== 'genres') {
      const foundedYear = this.form.value.foundedYear;
      if (foundedYear !== null && foundedYear !== undefined && foundedYear < 1900) {
        this.error = 'Founded year must be at least 1900.';
        return false;
      }
    }

    this.error = null;
    return true;
  }

  async submit() {
    if (!this.validateForm()) {
      return;
    }

    try {
      this.submitting = true;
      const payload = this.buildPayload();

      if (this.entityType === 'genres') {
        if (this.editingId) {
          await firstValueFrom(this.genreService.update(this.editingId, payload));
        } else {
          await firstValueFrom(this.genreService.create(payload));
        }
      }

      if (this.entityType === 'production-companies') {
        if (this.editingId) {
          await firstValueFrom(this.companyService.update(this.editingId, payload));
        } else {
          await firstValueFrom(this.companyService.create(payload));
        }
      }

      if (this.entityType === 'media-outlets') {
        if (this.editingId) {
          await firstValueFrom(this.mediaOutletService.update(this.editingId, payload));
        } else {
          await firstValueFrom(this.mediaOutletService.create(payload));
        }
      }

      await this.loadRows();
      this.resetForm();
      this.changed = true;
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to save record.';
    } finally {
      this.submitting = false;
    }
  }

  async deleteRow(row: Genre | ProductionCompany | MediaOutlet) {
    const confirmed = window.confirm(`Delete "${row.name}"?`);
    if (!confirmed || !row.id) {
      return;
    }

    try {
      this.submitting = true;
      if (this.entityType === 'genres') {
        await firstValueFrom(this.genreService.delete(row.id));
      }

      if (this.entityType === 'production-companies') {
        await firstValueFrom(this.companyService.delete(row.id));
      }

      if (this.entityType === 'media-outlets') {
        await firstValueFrom(this.mediaOutletService.delete(row.id));
      }

      await this.loadRows();
      this.changed = true;
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to delete record.';
    } finally {
      this.submitting = false;
    }
  }

  close() {
    this.dialogRef.close({ changed: this.changed });
  }
}
