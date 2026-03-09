import DeleteOutlineRoundedIcon from '@mui/icons-material/DeleteOutlineRounded';
import EditRoundedIcon from '@mui/icons-material/EditRounded';
import RefreshRoundedIcon from '@mui/icons-material/RefreshRounded';
import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { useCallback, useEffect, useMemo, useState } from 'react';
import CompanyTypeService from '../../services/CompanyTypeService';
import GenreService from '../../services/GenreService';
import log from '../../services/Logger';
import MediaOutletService from '../../services/MediaOutletService';
import ProductionCompanyService from '../../services/ProductionCompanyService';
import { fetchAllPageContent } from '../../utils/fetchAllPageContent';

const ENTITY_TYPES = Object.freeze({
  GENRES: 'genres',
  PRODUCTION_COMPANIES: 'production-companies',
  MEDIA_OUTLETS: 'media-outlets',
});

const getEntityLabel = (entityType) => {
  if (entityType === ENTITY_TYPES.GENRES) {
    return 'Genres';
  }

  if (entityType === ENTITY_TYPES.PRODUCTION_COMPANIES) {
    return 'Production companies';
  }

  if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
    return 'Media outlets';
  }

  return 'Reference data';
};

const getDefaultFormValues = (entityType) => {
  if (entityType === ENTITY_TYPES.GENRES) {
    return { name: '' };
  }

  if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
    return { name: '', websiteUrl: '', foundedYear: '' };
  }

  return { name: '', foundedYear: '', websiteUrl: '', ceo: '', companyTypeId: '' };
};

const toNullableNumber = (value) => {
  if (value === '' || value === null || value === undefined) {
    return null;
  }

  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
};

function ReferenceManagerDialog({
  open,
  entityType,
  onClose,
  onChanged,
}) {
  const [rows, setRows] = useState([]);
  const [companyTypes, setCompanyTypes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState('');
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(getDefaultFormValues(entityType));

  const isEditing = editingId !== null;
  const label = getEntityLabel(entityType);

  const resetForm = useCallback(() => {
    setEditingId(null);
    setForm(getDefaultFormValues(entityType));
  }, [entityType]);

  const loadRows = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      if (entityType === ENTITY_TYPES.GENRES) {
        const response = await GenreService.getAll();
        setRows(Array.isArray(response.data) ? response.data : []);
        return;
      }

      if (entityType === ENTITY_TYPES.PRODUCTION_COMPANIES) {
        const allCompanies = await fetchAllPageContent(
          (params) => ProductionCompanyService.getAll(params),
          {
            sortBy: 'name',
            sortDirection: 'ASC',
          }
        );
        setRows(allCompanies);
        return;
      }

      if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
        const allOutlets = await fetchAllPageContent(
          (params) => MediaOutletService.getAll(params),
          {
            sortBy: 'name',
            sortDirection: 'ASC',
          }
        );
        setRows(allOutlets);
      }
    } catch (loadError) {
      log.error('Failed to load reference rows:', loadError);
      setError(loadError.message || 'Failed to load data.');
    } finally {
      setLoading(false);
    }
  }, [entityType]);

  const loadCompanyTypes = useCallback(async () => {
    if (entityType !== ENTITY_TYPES.PRODUCTION_COMPANIES) {
      setCompanyTypes([]);
      return;
    }

    try {
      const response = await CompanyTypeService.getAll();
      setCompanyTypes(Array.isArray(response.data) ? response.data : []);
    } catch (loadError) {
      log.warn('Failed to load company types:', loadError);
      setCompanyTypes([]);
    }
  }, [entityType]);

  useEffect(() => {
    if (!open) {
      return;
    }

    setSearch('');
    resetForm();
    loadCompanyTypes();
    loadRows();
  }, [loadCompanyTypes, loadRows, open, resetForm]);

  const filteredRows = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();
    if (!normalizedSearch) {
      return rows;
    }

    return rows.filter((item) => {
      const name = item.name?.toLowerCase() || '';
      const website = item.websiteUrl?.toLowerCase() || '';
      const ceo = item.ceo?.toLowerCase() || '';
      return (
        name.includes(normalizedSearch) ||
        website.includes(normalizedSearch) ||
        ceo.includes(normalizedSearch)
      );
    });
  }, [rows, search]);

  const emptyStateColSpan = useMemo(() => {
    if (entityType === ENTITY_TYPES.GENRES) {
      return 3;
    }

    if (entityType === ENTITY_TYPES.PRODUCTION_COMPANIES) {
      return 7;
    }

    return 5;
  }, [entityType]);

  const handleFieldChange = (fieldName) => (event) => {
    setForm((prev) => ({ ...prev, [fieldName]: event.target.value }));
  };

  const handleEdit = (row) => {
    setEditingId(row.id);

    if (entityType === ENTITY_TYPES.GENRES) {
      setForm({ name: row.name || '' });
      return;
    }

    if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
      setForm({
        name: row.name || '',
        websiteUrl: row.websiteUrl || '',
        foundedYear: row.foundedYear ?? '',
      });
      return;
    }

    setForm({
      name: row.name || '',
      foundedYear: row.foundedYear ?? '',
      websiteUrl: row.websiteUrl || '',
      ceo: row.ceo || '',
      companyTypeId: row.companyTypeId ?? '',
    });
  };

  const buildPayload = () => {
    if (entityType === ENTITY_TYPES.GENRES) {
      return { name: form.name.trim() };
    }

    if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
      return {
        name: form.name.trim(),
        websiteUrl: form.websiteUrl?.trim() || null,
        foundedYear: toNullableNumber(form.foundedYear),
      };
    }

    return {
      name: form.name.trim(),
      foundedYear: toNullableNumber(form.foundedYear),
      websiteUrl: form.websiteUrl?.trim() || null,
      ceo: form.ceo?.trim() || null,
      companyTypeId: toNullableNumber(form.companyTypeId),
    };
  };

  const validateForm = () => {
    if (!form.name?.trim()) {
      setError('Name is required.');
      return false;
    }

    setError(null);
    return true;
  };

  const handleSubmit = async () => {
    if (!validateForm()) {
      return;
    }

    try {
      setSubmitting(true);
      const payload = buildPayload();

      if (entityType === ENTITY_TYPES.GENRES) {
        if (isEditing) {
          await GenreService.update(editingId, payload);
        } else {
          await GenreService.create(payload);
        }
      }

      if (entityType === ENTITY_TYPES.PRODUCTION_COMPANIES) {
        if (isEditing) {
          await ProductionCompanyService.update(editingId, payload);
        } else {
          await ProductionCompanyService.create(payload);
        }
      }

      if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
        if (isEditing) {
          await MediaOutletService.update(editingId, payload);
        } else {
          await MediaOutletService.create(payload);
        }
      }

      await loadRows();
      resetForm();
      if (onChanged) {
        onChanged();
      }
    } catch (submitError) {
      log.error('Failed to submit reference form:', submitError);
      setError(submitError.message || 'Failed to save record.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (rowId, rowName) => {
    const confirmed = window.confirm(`Delete "${rowName}"?`);
    if (!confirmed) {
      return;
    }

    try {
      setSubmitting(true);
      setError(null);

      if (entityType === ENTITY_TYPES.GENRES) {
        await GenreService.delete(rowId);
      }

      if (entityType === ENTITY_TYPES.PRODUCTION_COMPANIES) {
        await ProductionCompanyService.delete(rowId);
      }

      if (entityType === ENTITY_TYPES.MEDIA_OUTLETS) {
        await MediaOutletService.delete(rowId);
      }

      await loadRows();
      if (onChanged) {
        onChanged();
      }
    } catch (deleteError) {
      log.error('Failed to delete reference row:', deleteError);
      setError(deleteError.message || 'Failed to delete record.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
      <DialogTitle>{label} manager</DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2}>
          {error && <Alert severity="error">{error}</Alert>}

          <Paper variant="outlined" sx={{ p: 2 }}>
            <Stack spacing={2}>
              <Typography variant="h6">
                {isEditing ? 'Edit record' : 'Create record'}
              </Typography>

              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                <TextField
                  label="Name"
                  value={form.name || ''}
                  onChange={handleFieldChange('name')}
                  required
                  fullWidth
                />

                {entityType !== ENTITY_TYPES.GENRES && (
                  <TextField
                    label="Website URL"
                    value={form.websiteUrl || ''}
                    onChange={handleFieldChange('websiteUrl')}
                    fullWidth
                  />
                )}

                {entityType !== ENTITY_TYPES.GENRES && (
                  <TextField
                    label="Founded year"
                    type="number"
                    value={form.foundedYear ?? ''}
                    onChange={handleFieldChange('foundedYear')}
                    slotProps = {{htmlInput : { min: 1900 }}}
                    sx={{ minWidth: 150 }}
                  />
                )}

                {entityType === ENTITY_TYPES.PRODUCTION_COMPANIES && (
                  <TextField
                    label="CEO"
                    value={form.ceo || ''}
                    onChange={handleFieldChange('ceo')}
                    sx={{ minWidth: 180 }}
                  />
                )}

                {entityType === ENTITY_TYPES.PRODUCTION_COMPANIES && (
                  <FormControl sx={{ minWidth: 180 }}>
                    <InputLabel id="company-type-select-label">Company type</InputLabel>
                    <Select
                      labelId="company-type-select-label"
                      label="Company type"
                      value={form.companyTypeId ?? ''}
                      onChange={handleFieldChange('companyTypeId')}
                    >
                      <MenuItem value="">None</MenuItem>
                      {companyTypes.map((companyType) => (
                        <MenuItem key={companyType.id} value={companyType.id}>
                          {companyType.name}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                )}
              </Stack>

              <Stack direction="row" spacing={1}>
                <Button
                  variant="contained"
                  onClick={handleSubmit}
                  disabled={submitting}
                >
                  {isEditing ? 'Save' : 'Create'}
                </Button>
                {isEditing && (
                  <Button variant="outlined" onClick={resetForm} disabled={submitting}>
                    Cancel edit
                  </Button>
                )}
              </Stack>
            </Stack>
          </Paper>

          <Stack direction={{ xs: 'column', md: 'row' }} spacing={1}>
            <TextField
              label="Search"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              fullWidth
            />
            <Button
              variant="outlined"
              startIcon={<RefreshRoundedIcon />}
              onClick={loadRows}
              disabled={loading}
            >
              Refresh
            </Button>
          </Stack>

          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>ID</TableCell>
                  <TableCell>Name</TableCell>
                  {entityType !== ENTITY_TYPES.GENRES && <TableCell>Website</TableCell>}
                  {entityType !== ENTITY_TYPES.GENRES && <TableCell>Founded</TableCell>}
                  {entityType === ENTITY_TYPES.PRODUCTION_COMPANIES && <TableCell>CEO</TableCell>}
                  {entityType === ENTITY_TYPES.PRODUCTION_COMPANIES && <TableCell>Company type</TableCell>}
                  <TableCell align="right">Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {filteredRows.map((row) => (
                  <TableRow key={row.id}>
                    <TableCell>{row.id}</TableCell>
                    <TableCell>{row.name}</TableCell>
                    {entityType !== ENTITY_TYPES.GENRES && <TableCell>{row.websiteUrl || '-'}</TableCell>}
                    {entityType !== ENTITY_TYPES.GENRES && <TableCell>{row.foundedYear || '-'}</TableCell>}
                    {entityType === ENTITY_TYPES.PRODUCTION_COMPANIES && <TableCell>{row.ceo || '-'}</TableCell>}
                    {entityType === ENTITY_TYPES.PRODUCTION_COMPANIES && <TableCell>{row.companyTypeName || '-'}</TableCell>}
                    <TableCell align="right">
                      <IconButton size="small" onClick={() => handleEdit(row)}>
                        <EditRoundedIcon fontSize="small" />
                      </IconButton>
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => handleDelete(row.id, row.name)}
                      >
                        <DeleteOutlineRoundedIcon fontSize="small" />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
                {filteredRows.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={emptyStateColSpan}>
                      <Typography color="text.secondary" variant="body2">
                        No records found.
                      </Typography>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
}

export default ReferenceManagerDialog;
