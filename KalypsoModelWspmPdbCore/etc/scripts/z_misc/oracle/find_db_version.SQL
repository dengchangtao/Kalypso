-- anmelden als pdb/pdb (andere user ggf. testen)
--select * from v$version where banner like 'Oracle%'; -- Ergebnis: 'Oracle Database 11g Release 11.2.0.1.0 - 64bit '
select version from product_component_version where product like 'Oracle%'; -- Ergebnis: 11.2.0.1.0