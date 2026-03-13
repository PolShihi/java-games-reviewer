import BuildRoundedIcon from '@mui/icons-material/BuildRounded';
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
import MediaOutletService from '../../services/MediaOutletService';
import log from '../../services/Logger';
import ReviewService from '../../services/ReviewService';
import { getApiErrorMessage } from '../../utils/apiError';
import { fetchAllPageContent } from '../../utils/fetchAllPageContent';
import ReferenceManagerDialog from '../reference/ReferenceManagerDialog';

const DEFAULT_FORM = Object.freeze({
  mediaOutletId: '',
  score: '',
  summary: '',
});

function AddReviewDialog({
  open,
  gameId,
  initialReview = null,
  onClose,
  onCreated,
}) {
  const [outlets, setOutlets] = useState([]);
  const [form, setForm] = useState(DEFAULT_FORM);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [outletsManagerOpen, setOutletsManagerOpen] = useState(false);

  const isEditMode = Boolean(initialReview?.id);

  const canSubmit = useMemo(() => {
    return Boolean(form.mediaOutletId !== '' && form.score !== '');
  }, [form.mediaOutletId, form.score]);

  const loadOutlets = async () => {
    const content = await fetchAllPageContent(
      (params) => MediaOutletService.getAll(params),
      {
        sortBy: 'name',
        sortDirection: 'ASC',
      }
    );
    setOutlets(content);
  };

  useEffect(() => {
    if (!open) {
      return;
    }

    const load = async () => {
      try {
        setError(null);
        await loadOutlets();
      } catch (loadError) {
        log.error('Failed to load media outlets:', loadError);
        setError(loadError.message || 'Failed to load media outlets.');
      }
    };

    load();
  }, [open]);

  useEffect(() => {
    if (!open) {
      return;
    }

    if (!initialReview) {
      setForm(DEFAULT_FORM);
      return;
    }

    setForm({
      mediaOutletId: initialReview.mediaOutlet?.id ?? '',
      score: initialReview.score ?? '',
      summary: initialReview.summary || '',
    });
  }, [initialReview, open]);

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
    const score = Number(form.score);
    if (!form.mediaOutletId) {
      setError('Media outlet is required.');
      return false;
    }
    if (Number.isNaN(score) || score < 0 || score > 100) {
      setError('Score must be between 0 and 100.');
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
        mediaOutletId: Number(form.mediaOutletId),
        score: Number(form.score),
        summary: form.summary?.trim() || null,
      };

      if (isEditMode) {
        if (!initialReview?.id) {
          throw new Error('Review id is required for update.');
        }
        await ReviewService.update(initialReview.id, payload);
      } else {
        await ReviewService.create(payload);
      }

      resetState();
      if (onCreated) {
        await onCreated();
      }
      onClose();
    } catch (submitError) {
      log.error('Failed to save review:', submitError);
      setError(getApiErrorMessage(submitError, 'Failed to save review.'));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
        <DialogTitle>{isEditMode ? 'Edit Review' : 'Add Review'}</DialogTitle>
        <DialogContent dividers>
          <Stack spacing={2} sx={{ mt: 0.5 }}>
            {error && <Alert severity="error">{error}</Alert>}

            <Stack direction={{ xs: 'column', md: 'row' }} spacing={1}>
              <FormControl fullWidth>
                <InputLabel id="review-outlet-select-label">Media outlet</InputLabel>
                <Select
                  labelId="review-outlet-select-label"
                  label="Media outlet"
                  value={form.mediaOutletId}
                  onChange={handleFieldChange('mediaOutletId')}
                >
                  {outlets.map((outlet) => (
                    <MenuItem key={outlet.id} value={outlet.id}>
                      {outlet.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
              <Button
                variant="outlined"
                startIcon={<BuildRoundedIcon />}
                onClick={() => setOutletsManagerOpen(true)}
              >
                Manage
              </Button>
            </Stack>

            <TextField
              label="Score (0-100)"
              type="number"
              value={form.score}
              onChange={handleFieldChange('score')}
              slotProps={{ htmlInput: { min: 0, max: 100 } }}
              fullWidth
              required
            />

            <TextField
              label="Summary"
              value={form.summary}
              onChange={handleFieldChange('summary')}
              multiline
              minRows={3}
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

      <ReferenceManagerDialog
        open={outletsManagerOpen}
        entityType="media-outlets"
        onClose={() => setOutletsManagerOpen(false)}
        onChanged={loadOutlets}
      />
    </>
  );
}

export default AddReviewDialog;

AddReviewDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  gameId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  initialReview: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onCreated: PropTypes.func,
};

