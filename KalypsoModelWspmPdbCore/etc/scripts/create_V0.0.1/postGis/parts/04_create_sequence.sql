CREATE SEQUENCE seq_pdb
   INCREMENT 1
   START 1;
ALTER TABLE seq_pdb OWNER TO pdb_admin;

-- so würde die Sequence an Spalte gekoppelt
-- alter table waterlevel_fixation alter column id set default nextval('seq_pdb');
