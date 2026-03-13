import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { firstValueFrom } from 'rxjs';
import { SystemRequirement } from '../../core/models/system-requirement';
import { SystemRequirementType } from '../../core/models/system-requirement-type';
import { SystemRequirementService } from '../../core/services/system-requirement.service';
import { SystemRequirementTypeService } from '../../core/services/system-requirement-type.service';
import { getApiErrorMessage } from '../../core/utils/validation-error';
import { ApiError } from '../../core/http/api-error';

export interface SystemRequirementDialogData {
  gameId: number;
  requirement?: SystemRequirement | null;
}

@Component({
  selector: 'app-system-requirement-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './system-requirement-dialog.component.html',
  styleUrl: './system-requirement-dialog.component.scss',
})
export class SystemRequirementDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<SystemRequirementDialogComponent>);
  private readonly requirementService = inject(SystemRequirementService);
  private readonly requirementTypeService = inject(SystemRequirementTypeService);

  readonly gameId: number;
  readonly initialRequirement: SystemRequirement | null;

  types: SystemRequirementType[] = [];
  error: string | null = null;
  submitting = false;

  readonly form = this.fb.group({
    systemRequirementTypeId: this.fb.control<number | null>(null, [Validators.required]),
    storageGb: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)]),
    ramGb: this.fb.control<number | null>(null, [Validators.required, Validators.min(1)]),
    cpuGhz: this.fb.control<number | null>(null),
    gpuTflops: this.fb.control<number | null>(null),
    vramGb: this.fb.control<number | null>(null),
  });

  constructor(@Inject(MAT_DIALOG_DATA) data: SystemRequirementDialogData) {
    this.gameId = data.gameId;
    this.initialRequirement = data.requirement ?? null;
  }

  get title(): string {
    return this.initialRequirement?.id ? 'Edit System Requirement' : 'Add System Requirement';
  }

  ngOnInit(): void {
    this.loadTypes();
    this.prefillForm();
  }

  private async loadTypes(): Promise<void> {
    try {
      const response = await firstValueFrom(this.requirementTypeService.getAll());
      this.types = Array.isArray(response) ? response : [];
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to load requirement types.';
    }
  }

  private prefillForm(): void {
    if (!this.initialRequirement) {
      return;
    }

    this.form.patchValue({
      systemRequirementTypeId: this.initialRequirement.type?.id ?? null,
      storageGb: this.initialRequirement.storageGb ?? null,
      ramGb: this.initialRequirement.ramGb ?? null,
      cpuGhz: this.initialRequirement.cpuGhz ?? null,
      gpuTflops: this.initialRequirement.gpuTflops ?? null,
      vramGb: this.initialRequirement.vramGb ?? null,
    });
  }

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    try {
      this.submitting = true;
      this.error = null;

      const value = this.form.getRawValue();
      const payload = {
        gameId: this.gameId,
        systemRequirementTypeId: Number(value.systemRequirementTypeId),
        storageGb: Number(value.storageGb),
        ramGb: Number(value.ramGb),
        cpuGhz: value.cpuGhz ?? null,
        gpuTflops: value.gpuTflops ?? null,
        vramGb: value.vramGb ?? null,
      };

      if (this.initialRequirement?.id) {
        await firstValueFrom(this.requirementService.update(this.initialRequirement.id, payload));
      } else {
        await firstValueFrom(this.requirementService.create(payload));
      }

      this.dialogRef.close({ changed: true });
    } catch (error) {
      this.error = getApiErrorMessage(error, 'Failed to save requirement.');
    } finally {
      this.submitting = false;
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}
