-- Idempotent upserts for POST /baselines/recompute (CG-65). baseline_dev only; public untouched.
create unique index if not exists baselines_region_signal_window_uidx
  on baseline_dev.baselines (region, signal_type, window_start, window_end);
