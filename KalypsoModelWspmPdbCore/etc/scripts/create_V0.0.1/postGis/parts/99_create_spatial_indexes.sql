-- ggf. noch ein re-create machen (oder erst droppen und dann neu)
CREATE INDEX IX_POINT__LOCATION ON POINT USING GIST ( LOCATION );
CREATE INDEX IX_CROSS_SECTION_PART__LINE ON CROSS_SECTION_PART USING GIST ( LINE );
CREATE INDEX IX_CROSS_SECTION__LINE ON CROSS_SECTION USING GIST ( LINE );
CREATE INDEX IX_WATER_BODY__RIVERLINE ON WATER_BODY USING GIST ( RIVERLINE );
CREATE INDEX IX_WL_FIX__LOCATION ON WATERLEVEL_FIXATION USING GIST ( LOCATION );


-- Statistiken sammeln
--VACUUM ANALYZE [table_name] [column_name];
