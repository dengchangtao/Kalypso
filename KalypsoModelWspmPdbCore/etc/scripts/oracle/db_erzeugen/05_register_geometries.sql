INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('POINT','LOCATION',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('X',4300000,4600000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Y',5600000,5800000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Z',-1000,10000,0.0005)
), 31468
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('CROSS_SECTION_PART','LINE',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('X',4300000,4600000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Y',5600000,5800000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Z',-1000,10000,0.0005)
), 31468
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('CROSS_SECTION','LINE',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('X',4300000,4600000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Y',5600000,5800000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Z',-1000,10000,0.0005)
), 31468
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('WATER_BODY','RIVERLINE',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('X',4300000,4600000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Y',5600000,5800000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Z',-1000,10000,0.0005)
), 31468
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('WATERLEVEL_FIXATION','LOCATION',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('X',4300000,4600000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Y',5600000,5800000,0.0005),
MDSYS.SDO_DIM_ELEMENT('Z',-1000,10000,0.0005)
), 31468
);
