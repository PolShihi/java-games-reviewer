import { SystemRequirementType } from './system-requirement-type';

export interface SystemRequirement {
  id: number;
  gameId: number;
  type: SystemRequirementType | null;
  storageGb: number | null;
  ramGb: number | null;
  cpuGhz: number | null;
  gpuTflops: number | null;
  vramGb: number | null;
}

export interface SystemRequirementCreateRequest {
  gameId: number;
  systemRequirementTypeId: number;
  storageGb: number;
  ramGb: number;
  cpuGhz?: number | null;
  gpuTflops?: number | null;
  vramGb?: number | null;
}
