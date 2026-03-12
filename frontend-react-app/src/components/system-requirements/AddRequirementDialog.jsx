import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Stack,
  TextField,
} from '@mui/material';
import PropTypes from 'prop-types';
import { useEffect, useMemo, useState } from 'react';
import log from '../../services/Logger';
import SystemRequirementService from '../../services/SystemRequirementService';
import SystemRequirementTypeService from '../../services/SystemRequirementTypeService';
import { getApiErrorMessage } from '../../utils/apiError';

const DEFAULT_FORM = Object.freeze({
  systemRequirementTypeId: '',
  storageGb: '',
  ramGb: '',
  cpuGhz: '',
  gpuTflops: '',
  vramGb: '',
});

const toNullableNumber = (value) => {
  if (value === '' || value === null || value === undefined) {
    return null;
  }

  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
};

const hasValidScale = (value, maxDecimals) => {
  if (value === '' || value === null || value === undefined) {
    return true;
  }
  const text = String(value);
  const parts = text.split('.');
  return parts.length < 2 || parts[1].length <= maxDecimals;
};

function AddRequirementDialog({
  open,
  gameId,
  initialRequirement = null,
  onClose,
  onCreated,
}) {
  const [types, setTypes] = useState([]);
  const [form, setForm] = useState(DEFAULT_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const isEditMode = Boolean(initialRequirement?.id);

  const canSubmit = useMemo(() => {
    return Boolean(
      form.systemRequirementTypeId &&
        form.storageGb &&
        form.ramGb
    );
  }, [form]);

  useEffect(() => {
    if (!open) {
      return;
    }

    const loadTypes = async () => {
      try {
        setError(null);
        const response = await SystemRequirementTypeService.getAll();
        setTypes(Array.isArray(response.data) ? response.data : []);
      } catch (loadError) {
        log.error('Failed to load system requirement types:', loadError);
        setError(loadError.message || 'Failed to load requirement types.');
      }
    };

    loadTypes();
  }, [open]);

  useEffect(() => {
    if (!open) {
      return;
    }

    if (!initialRequirement) {
      setForm(DEFAULT_FORM);
      return;
    }

    setForm({
      systemRequirementTypeId: initialRequirement.type?.id ?? '',
      storageGb: initialRequirement.storageGb ?? '',
      ramGb: initialRequirement.ramGb ?? '',
      cpuGhz: initialRequirement.cpuGhz ?? '',
      gpuTflops: initialRequirement.gpuTflops ?? '',
      vramGb: initialRequirement.vramGb ?? '',
    });
  }, [initialRequirement, open]);

  const resetState = () => {
    setForm(DEFAULT_FORM);
    setError(null);
  };

  const handleClose = () => {
    if (submitting) {
      return;
    }
    resetState();
    onClose();
  };

  const handleFieldChange = (field) => (event) => {
    setForm((prev) => ({ ...prev, [field]: event.target.value }));
  };

  const validate = () => {
    const storage = Number(form.storageGb);
    const ram = Number(form.ramGb);
    const cpu = toNullableNumber(form.cpuGhz);
    const gpu = toNullableNumber(form.gpuTflops);
    const vram = toNullableNumber(form.vramGb);

    if (!form.systemRequirementTypeId) {
      setError('Requirement type is required.');
      return false;
    }
    if (Number.isNaN(storage) || storage < 1) {
      setError('Storage must be at least 1 GB.');
      return false;
    }
    if (Number.isNaN(ram) || ram < 1) {
      setError('RAM must be at least 1 GB.');
      return false;
    }
    if (storage > 2147483647 || ram > 2147483647) {
      setError('Storage and RAM values are too large.');
      return false;
    }
    if (vram !== null && (vram < 0 || vram > 2147483647)) {
      setError('VRAM value is too large.');
      return false;
    }
    if (cpu !== null) {
      if (!hasValidScale(form.cpuGhz, 1)) {
        setError('CPU GHz must have up to 1 decimal place.');
        return false;
      }
      if (cpu < 0 || cpu > 99.9) {
        setError('CPU GHz must be between 0.0 and 99.9.');
        return false;
      }
    }
    if (gpu !== null) {
      if (!hasValidScale(form.gpuTflops, 2)) {
        setError('GPU TFLOPS must have up to 2 decimal places.');
        return false;
      }
      if (gpu < 0 || gpu > 99.99) {
        setError('GPU TFLOPS must be between 0.0 and 99.99.');
        return false;
      }
    }
    setError(null);
    return true;
  };

  const handleSubmit = async () => {
    if (!validate()) {
      return;
    }

    try {
      setSubmitting(true);
      const payload = {
        gameId: Number(gameId),
        systemRequirementTypeId: Number(form.systemRequirementTypeId),
        storageGb: Number(form.storageGb),
        ramGb: Number(form.ramGb),
        cpuGhz: toNullableNumber(form.cpuGhz),
        gpuTflops: toNullableNumber(form.gpuTflops),
        vramGb: toNullableNumber(form.vramGb),
      };

      if (isEditMode) {
        await SystemRequirementService.update(initialRequirement.id, payload);
      } else {
        await SystemRequirementService.create(payload);
      }
      resetState();
      if (onCreated) {
        await onCreated();
      }
      onClose();
    } catch (submitError) {
      log.error('Failed to save system requirement:', submitError);
      setError(getApiErrorMessage(submitError, 'Failed to save system requirement.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>{isEditMode ? 'Edit System Requirement' : 'Add System Requirement'}</DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2} sx={{ mt: 0.5 }}>
          {error && <Alert severity="error">{error}</Alert>}

          <FormControl fullWidth>
            <InputLabel id="requirement-type-select-label">Requirement type</InputLabel>
            <Select
              labelId="requirement-type-select-label"
              label="Requirement type"
              value={form.systemRequirementTypeId}
              onChange={handleFieldChange('systemRequirementTypeId')}
            >
              {types.map((type) => (
                <MenuItem key={type.id} value={type.id}>
                  {type.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <TextField
            label="Storage (GB)"
            type="number"
            value={form.storageGb}
            onChange={handleFieldChange('storageGb')}
            slotProps={{ htmlInput: { min: 1, max: 2147483647 } }}
            required
            fullWidth
          />

          <TextField
            label="RAM (GB)"
            type="number"
            value={form.ramGb}
            onChange={handleFieldChange('ramGb')}
            slotProps={{ htmlInput: { min: 1, max: 2147483647 } }}
            required
            fullWidth
          />

          <TextField
            label="CPU (GHz)"
            type="number"
            value={form.cpuGhz}
            onChange={handleFieldChange('cpuGhz')}
            slotProps={{ htmlInput: { min: 0, max: 99.9, step: 0.1 } }}
            fullWidth
          />

          <TextField
            label="GPU (TFLOPS)"
            type="number"
            value={form.gpuTflops}
            onChange={handleFieldChange('gpuTflops')}
            slotProps={{ htmlInput: { min: 0, max: 99.99, step: 0.01 } }}
            fullWidth
          />

          <TextField
            label="VRAM (GB)"
            type="number"
            value={form.vramGb}
            onChange={handleFieldChange('vramGb')}
            slotProps={{ htmlInput: { min: 0, max: 2147483647 } }}
            fullWidth
          />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={submitting}>
          Cancel
        </Button>
        <Button variant="contained" onClick={handleSubmit} disabled={submitting || !canSubmit}>
          {isEditMode ? 'Save' : 'Add'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AddRequirementDialog;

AddRequirementDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  gameId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  initialRequirement: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onCreated: PropTypes.func,
};
