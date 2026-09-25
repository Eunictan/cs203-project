# baseline_dev migrations (Supabase)

Applied to the Supabase project in filename order; the timestamp prefix is the version recorded in
Supabase's migration history. Deliberately NOT in `backend/src/main/resources/db/migration`: Spring's
Flyway runs that folder against the local/CI Postgres, where the `service_role` role does not exist.

Not captured here: `20260924065157_create_baseline_dev_schema`, which created the schema and both tables
before these files existed. Its SQL is only in Supabase's migration history and should be exported into this
folder so a fresh environment can be rebuilt from scratch.
