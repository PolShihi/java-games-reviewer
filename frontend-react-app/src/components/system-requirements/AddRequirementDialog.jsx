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
import { useEffect, useMemo, useState } from 'react';
import log from '../../services/Logger';
import SystemRequirementService from '../../services/SystemRequirementService';
import SystemRequirementTypeService from '../../services/SystemRequirementTypeService';

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

function AddRequirementDialog({ open, gameId, onClose, onCreated }) {
  const [types, setTypes] = useState([]);
  const [form, setForm] = useState(DEFAULT_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

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

      await SystemRequirementService.create(payload);
      resetState();
      if (onCreated) {
        await onCreated();
      }
      onClose();
    } catch (submitError) {
      log.error('Failed to create system requirement:', submitError);
      setError(submitError.message || 'Failed to create system requirement.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Add System Requirement</DialogTitle>
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
            slotProps={{ htmlInput: { min: 1 } }}
            required
            fullWidth
          />

          <TextField
            label="RAM (GB)"
            type="number"
            value={form.ramGb}
            onChange={handleFieldChange('ramGb')}
            slotProps={{ htmlInput: { min: 1 } }}
            required
            fullWidth
          />

          <TextField
            label="CPU (GHz)"
            type="number"
            value={form.cpuGhz}
            onChange={handleFieldChange('cpuGhz')}
            slotProps={{ htmlInput: { min: 0, step: 0.1 } }}
            fullWidth
          />

          <TextField
            label="GPU (TFLOPS)"
            type="number"
            value={form.gpuTflops}
            onChange={handleFieldChange('gpuTflops')}
            slotProps={{ htmlInput: { min: 0, step: 0.01 } }}
            fullWidth
          />

          <TextField
            label="VRAM (GB)"
            type="number"
            value={form.vramGb}
            onChange={handleFieldChange('vramGb')}
            slotProps={{ htmlInput: { min: 0 } }}
            fullWidth
          />
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={submitting}>
          Cancel
        </Button>
        <Button variant="contained" onClick={handleSubmit} disabled={submitting || !canSubmit}>
          Add
        </Button>
      </DialogActions>
    </Dialog>
  );
}

export default AddRequirementDialog;

