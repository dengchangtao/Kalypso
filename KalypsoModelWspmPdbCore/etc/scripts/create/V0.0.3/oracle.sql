-- V0.0.1

-- 03_create_tables

-- Generated by Oracle SQL Developer Data Modeler 3.0.0.665
--   at:        2011-06-07 13:15:59 MESZ
--   site:      Oracle Database 10g
--   type:      Oracle Database 10g



CREATE TABLE Cross_Section
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (50)  NOT NULL ,
     Line MDSYS.SDO_GEOMETRY ,
     Station NUMBER (8,1)  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User VARCHAR2 (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) , 
     Description VARCHAR2 (255) ,
     Water_Body NUMBER (20)  NOT NULL ,
     State NUMBER (20)  NOT NULL
    )
;


CREATE UNIQUE INDEX "Cross_Section PKX" ON Cross_Section
    (
     ID ASC
    )
;
CREATE UNIQUE INDEX "Cross_Section Name UKX" ON Cross_Section
    (
     Name ASC ,
     State ASC
    )
;
CREATE INDEX State__CSX ON Cross_Section
    (
     State ASC
    )
;
CREATE INDEX Water_Body__CSX ON Cross_Section
    (
     Water_Body ASC
    )
;

ALTER TABLE Cross_Section
    ADD CONSTRAINT "Cross_Section PK" PRIMARY KEY ( ID ) ;

ALTER TABLE Cross_Section
    ADD CONSTRAINT "Cross_Section Name UK" UNIQUE ( Name , State ) ;


CREATE TABLE Cross_Section_Part
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (50)  NOT NULL ,
     Line MDSYS.SDO_GEOMETRY ,
     Category VARCHAR2 (50)  NOT NULL ,
     Description VARCHAR2 (255) ,
     Cross_Section NUMBER (20)  NOT NULL
    )
;

CREATE UNIQUE INDEX "Cross_Section_Part PKX" ON Cross_Section_Part
    (
     ID ASC
    )
;
CREATE UNIQUE INDEX "Cross_Section_Part Name UKX" ON Cross_Section_Part
    (
     Name ASC ,
     Cross_Section ASC
    )
;
CREATE INDEX CS__CS_PartX ON Cross_Section_Part
    (
     Cross_Section ASC
    )
;

ALTER TABLE Cross_Section_Part
    ADD CONSTRAINT "Cross_Section_Part PK" PRIMARY KEY ( ID ) ;

ALTER TABLE Cross_Section_Part
    ADD CONSTRAINT "Cross_Section_Part Name UK" UNIQUE ( Name , Cross_Section ) ;


CREATE TABLE Event
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (100)  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User VARCHAR2 (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Source VARCHAR2 (255) ,
     Type VARCHAR2 (50) DEFAULT 'Measurement' CHECK ( Type IN ('Measurement', 'Other', 'Simulation')) ,
     Description VARCHAR2 (255) ,
     Water_Body NUMBER (20)  NOT NULL
    )
;


CREATE UNIQUE INDEX "Event PKX" ON Event
    (
     ID ASC
    )
;
CREATE UNIQUE INDEX "Event Name UKX" ON Event
    (
     Name ASC
    )
;
CREATE INDEX Water_Body__EventX ON Event
    (
     Water_Body ASC
    )
;

ALTER TABLE Event
    ADD CONSTRAINT "Event PK" PRIMARY KEY ( ID ) ;

ALTER TABLE Event
    ADD CONSTRAINT "Event Name UK" UNIQUE ( Name ) ;


CREATE TABLE Info
    (
     Key VARCHAR2 (50)  NOT NULL ,
     Value VARCHAR2 (255)
    )
;


CREATE UNIQUE INDEX "Info PKX" ON Info
    (
     Key ASC
    )
;

ALTER TABLE Info
    ADD CONSTRAINT "Info PK" PRIMARY KEY ( Key ) ;

    -- Version setzen auf "creating" oder "updateing"
INSERT INTO INFO("KEY", "VALUE") VALUES ('Version', 'creating 0.0.1');

CREATE TABLE Point
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (50)  NOT NULL ,
     Location MDSYS.SDO_GEOMETRY ,
     Consecutive_Num NUMBER (11)  NOT NULL ,
     Code VARCHAR2 (50) ,
     HyK VARCHAR2 (50) ,
     Width NUMBER (8,4) ,
     Height NUMBER (8,4) ,
     Roughness_Point_Kind VARCHAR2 (50) ,
     Roughness_K_Value NUMBER (8,1) ,
     Roughness_Kst_Value NUMBER (8,1) ,
     Vegetation_Dp NUMBER (8,3) ,
     Vegetation_Ax NUMBER (8,3) ,
     Vegetation_Ay NUMBER (8,3) ,
     Description VARCHAR2 (255) ,
     Vegetation_Point_Kind VARCHAR2 (50) ,
     Vegetation VARCHAR2 (50) ,
     Roughness VARCHAR2 (50) ,
     Cross_Section_Part NUMBER (20)  NOT NULL
    )
;


CREATE UNIQUE INDEX "Point PKX" ON Point
    (
     ID ASC
    )
;
CREATE INDEX CS_Part__PointX ON Point
    (
     Cross_Section_Part ASC
    )
;
CREATE INDEX Roughness__PointX ON Point
    (
     Roughness_Point_Kind ASC ,
     Roughness ASC
    )
;
CREATE INDEX Vegetation__PointX ON Point
    (
     Vegetation_Point_Kind ASC ,
     Vegetation ASC
    )
;

ALTER TABLE Point
    ADD CONSTRAINT "Point PK" PRIMARY KEY ( ID ) ;


CREATE TABLE Point_Kind
    (
     Name VARCHAR2 (50)  NOT NULL ,
     Label VARCHAR2 (100)  NOT NULL ,
     Description VARCHAR2 (255)
    )
;


CREATE UNIQUE INDEX "Point_Kind PKX" ON Point_Kind
    (
     Name ASC
    )
;

ALTER TABLE Point_Kind
    ADD CONSTRAINT "Point_Kind PK" PRIMARY KEY ( Name ) ;


CREATE TABLE Roughness
    (
     Name VARCHAR2 (50)  NOT NULL ,
     K_Value NUMBER (8,1) ,
     Kst_Value NUMBER (8,1) ,
     Label VARCHAR2 (100) ,
     Source VARCHAR2 (255) ,
     Validity VARCHAR2 (255) ,
     Description VARCHAR2 (255) ,
     Point_Kind VARCHAR2 (50)  NOT NULL
    )
;


CREATE UNIQUE INDEX "Roughness PKX" ON Roughness
    (
     Point_Kind ASC ,
     Name ASC
    )
;
CREATE INDEX Point_Kind__RoughnessX ON Roughness
    (
     Point_Kind ASC
    )
;

ALTER TABLE Roughness
    ADD CONSTRAINT "Roughness PK" PRIMARY KEY ( Point_Kind, Name ) ;


CREATE TABLE State
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (100)  NOT NULL ,
     IsStateZero CHAR (1) DEFAULT 'F'  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User VARCHAR2 (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Source VARCHAR2 (255) ,
     Description VARCHAR2 (255)
    )
;


CREATE UNIQUE INDEX "State PKX" ON State
    (
     ID ASC
    )
;
CREATE UNIQUE INDEX "State Name UKX" ON State
    (
     Name ASC
    )
;

ALTER TABLE State
    ADD CONSTRAINT "State PK" PRIMARY KEY ( ID ) ;

ALTER TABLE State
    ADD CONSTRAINT "State Name UK" UNIQUE ( Name ) ;


CREATE TABLE Vegetation
    (
     Name VARCHAR2 (50)  NOT NULL ,
     Dp NUMBER (8,3)  NOT NULL ,
     Ax NUMBER (8,3)  NOT NULL ,
     Ay NUMBER (8,3)  NOT NULL ,
     Label VARCHAR2 (100) ,
     Source VARCHAR2 (255) ,
     Description VARCHAR2 (255) ,
     Point_Kind VARCHAR2 (50)  NOT NULL
    )
;


CREATE UNIQUE INDEX "Vegetation PKX" ON Vegetation
    (
     Point_Kind ASC ,
     Name ASC
    )
;
CREATE INDEX Point_Kind__VegetationX ON Vegetation
    (
     Point_Kind ASC
    )
;

ALTER TABLE Vegetation
    ADD CONSTRAINT "Vegetation PK" PRIMARY KEY ( Point_Kind, Name ) ;


CREATE TABLE Water_Body
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (100)  NOT NULL ,
     Riverline MDSYS.SDO_GEOMETRY ,
     Label VARCHAR2 (100)  NOT NULL ,
     Direction_of_Stationing VARCHAR2 (20) DEFAULT 'upstream'  NOT NULL CHECK ( Direction_of_Stationing IN ('downstream', 'upstream')) ,
     Rank INTEGER DEFAULT -1 ,
     Description VARCHAR2 (255)
    )
;



COMMENT ON COLUMN Water_Body.Name IS 'Gewaesserkennziffer (GKZ)'
;
CREATE UNIQUE INDEX "Water_Body Name UKX" ON Water_Body
    (
     Name ASC
    )
;
CREATE UNIQUE INDEX "Water_Body PKX" ON Water_Body
    (
     ID ASC
    )
;

ALTER TABLE Water_Body
    ADD CONSTRAINT "Water_Body PK" PRIMARY KEY ( ID ) ;

ALTER TABLE Water_Body
    ADD CONSTRAINT "Water_Body Name UK" UNIQUE ( Name ) ;


CREATE TABLE Waterlevel_Fixation
    (
     ID NUMBER (20)  NOT NULL ,
     Station NUMBER (8,1)  NOT NULL ,
     Location MDSYS.SDO_GEOMETRY ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User VARCHAR2 (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Waterlevel NUMBER (8,3) ,
     Discharge NUMBER (8,3) ,
     Description VARCHAR2 (255) ,
     Event NUMBER (20)  NOT NULL
    )
;


CREATE UNIQUE INDEX "Waterlevel_Fixation PKX" ON Waterlevel_Fixation
    (
     ID ASC
    )
;
CREATE INDEX Event__Waterlevel_FixationX ON Waterlevel_Fixation
    (
     Event ASC
    )
;

ALTER TABLE Waterlevel_Fixation
    ADD CONSTRAINT "Waterlevel_Fixation PK" PRIMARY KEY ( ID ) ;



ALTER TABLE Point
    ADD CONSTRAINT CS_Part__Point FOREIGN KEY
    (
     Cross_Section_Part
    )
    REFERENCES Cross_Section_Part
    (
     ID
    )
    ON DELETE CASCADE
;


ALTER TABLE Cross_Section_Part
    ADD CONSTRAINT CS__CS_Part FOREIGN KEY
    (
     Cross_Section
    )
    REFERENCES Cross_Section
    (
     ID
    )
    ON DELETE CASCADE
;


ALTER TABLE Waterlevel_Fixation
    ADD CONSTRAINT Event__Waterlevel_Fixation FOREIGN KEY
    (
     Event
    )
    REFERENCES Event
    (
     ID
    )
    ON DELETE CASCADE
;


ALTER TABLE Roughness
    ADD CONSTRAINT Point_Kind__Roughness FOREIGN KEY
    (
     Point_Kind
    )
    REFERENCES Point_Kind
    (
     Name
    )
    ON DELETE CASCADE
;


ALTER TABLE Vegetation
    ADD CONSTRAINT Point_Kind__Vegetation FOREIGN KEY
    (
     Point_Kind
    )
    REFERENCES Point_Kind
    (
     Name
    )
    ON DELETE CASCADE
;


ALTER TABLE Point
    ADD CONSTRAINT Roughness__Point FOREIGN KEY
    (
     Roughness_Point_Kind,
     Roughness
    )
    REFERENCES Roughness
    (
     Point_Kind,
     Name
    )
    ON DELETE SET NULL
;


ALTER TABLE Cross_Section
    ADD CONSTRAINT State__CS FOREIGN KEY
    (
     State
    )
    REFERENCES State
    (
     ID
    )
    ON DELETE CASCADE
;


ALTER TABLE Point
    ADD CONSTRAINT Vegetation__Point FOREIGN KEY
    (
     Vegetation_Point_Kind,
     Vegetation
    )
    REFERENCES Vegetation
    (
     Point_Kind,
     Name
    )
    ON DELETE SET NULL
;


ALTER TABLE Cross_Section
    ADD CONSTRAINT Water_Body__CS FOREIGN KEY
    (
     Water_Body
    )
    REFERENCES Water_Body
    (
     ID
    )
    ON DELETE CASCADE
;


ALTER TABLE Event
    ADD CONSTRAINT Water_Body__Event FOREIGN KEY
    (
     Water_Body
    )
    REFERENCES Water_Body
    (
     ID
    )
    ON DELETE CASCADE
;














-- Oracle SQL Developer Data Modeler Summary Report:
--
-- CREATE TABLE                            11
-- CREATE INDEX                            26
-- ALTER TABLE                             26
-- CREATE VIEW                              0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- CREATE STRUCTURED TYPE                   0
-- CREATE COLLECTION TYPE                   0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
--
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
--
-- ERRORS                                   0
-- WARNINGS                                 0

-- 04_create_sequence

create sequence "SEQ_PDB"
start with 1
increment by 1
nocache
nocycle
noorder
;

-- 05_register_geometries

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('POINT','LOCATION',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('CROSS_SECTION_PART','LINE',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('CROSS_SECTION','LINE',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('WATER_BODY','RIVERLINE',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);

INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('WATERLEVEL_FIXATION','LOCATION',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);


-- 06_prefill_table_info

INSERT INTO INFO("KEY", "VALUE") VALUES ('SRID', '${SRID}');
-- 07_prefill_table__point_kind
INSERT INTO point_kind (name, label, description) VALUES ('GAF', 'GAF', 'GAF');

-- 08_prefill_table__roughness
INSERT INTO roughness (name, label, description, point_kind) VALUES ('-1', 'unknown', 'unknown','GAF');

INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('1', 'glatt', 'glatt', 'GAF',0.001, 80);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('2', 'Feinsand, Schlamm', 'Feinsand, Schlamm', 'GAF',0.03, 55);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('3', 'Sand oder Feinkies', 'Sand oder Feinkies', 'GAF',0.05, 53);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('4', 'Feinkies', 'Feinkies', 'GAF',0.05, 50);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('5', 'mittlerer Kies', 'mittlerer Kies', 'GAF',0.08, 40);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('6', 'Schotter, mittlerer Grobkies, verkrautete Erdkan�le', 'Schotter, mittlerer Grobkies, verkrautete Erdkan�le', 'GAF',0.082, 35);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('7', 'Lehm, Wasserpflanzen', 'Lehm, Wasserpflanzen', 'GAF',0.1, 33);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('8', 'Steinsch�ttung,stark geschiebef�hrender Fluss,Wurzeln', 'Steinsch�ttung,stark geschiebef�hrender Fluss,Wurzeln', 'GAF',0.15, 30);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('9', 'Kiesanlandung, Wurzelgeflecht', 'Kiesanlandung, Wurzelgeflecht', 'GAF',0.2, 28);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('10', 'grobe Steine, Ger�llanlandung', 'grobe Steine, Ger�llanlandung', 'GAF',0.3, 25);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('11', 'Gebirgsfl�sse mit grobem Ger�ll, verkrautete Erdkan�le', 'Gebirgsfl�sse mit grobem Ger�ll, verkrautete Erdkan�le', 'GAF',0.45, 22.5);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('12', 'Fels', 'Fels', 'GAF',0.6, 20);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('13', 'Wildbach', 'Wildbach', 'GAF',0.9, 15);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('14', 'Wildbach mit starkem Geschiebetrieb, roher Felsausbruch', 'Wildbach mit starkem Geschiebetrieb, roher Felsausbruch', 'GAF',2, 12);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('20', 'Stahl, Zementputz gegl�ttet, Beton aus Vakuumschalung', 'Stahl, Zementputz gegl�ttet, Beton aus Vakuumschalung', 'GAF',0.001, 95);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('21', 'Holz, ungehobelt', 'Holz, ungehobelt', 'GAF',0.002, 90);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('22', 'Beton, glatt, Asphaltbeton, Klinker, sorgf�ltig verfugt', 'Beton, glatt, Asphaltbeton, Klinker, sorgf�ltig verfugt', 'GAF',0.003, 75);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('23', 'Ziegelmauerwerk, Rauputz, Verbundpflaster', 'Ziegelmauerwerk, Rauputz, Verbundpflaster', 'GAF',0.005, 70);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('24', 'Beton rauh, glatte Bruchsteine', 'Beton rauh, glatte Bruchsteine', 'GAF',0.015, 60);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('25', 'Pflaster, ARMCO-Profile', 'Pflaster, ARMCO-Profile', 'GAF',0.04, 50);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('26', 'Beton mit Fugen, grobes Bruchsteinmauerwerk', 'Beton mit Fugen, grobes Bruchsteinmauerwerk', 'GAF',0.02, 48);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('27', 'Natursteine, rauh', 'Natursteine, rauh', 'GAF',0.09, 40);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('28', 'Spundw�nde', 'Spundw�nde', 'GAF',0.06, 35);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('29', 'Schotter, Steinsch�ttung, Rasengittersteine', 'Schotter, Steinsch�ttung, Rasengittersteine', 'GAF',0.2, 30);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('30', 'grobe Steinsch�ttung', 'grobe Steinsch�ttung', 'GAF',0.4, 25);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('31', 'Steinsch�ttung mit Krautbewuchs', 'Steinsch�ttung mit Krautbewuchs', 'GAF',0.5, 23.5);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('32', 'Rauhe Sohlrampe', 'Rauhe Sohlrampe', 'GAF',1.5, 15);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('50', 'Rasen', 'Rasen', 'GAF',0.06, 40);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('51', 'Gras, Acker ohne Bewuchs', 'Gras, Acker ohne Bewuchs', 'GAF',0.2, 30);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('52', 'Waldboden', 'Waldboden', 'GAF',0.24, 27);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('53', 'Wiese, felsiger Waldboden', 'Wiese, felsiger Waldboden', 'GAF',0.25, 25);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('54', 'Gras mit Stauden', 'Gras mit Stauden', 'GAF',0.3, 24);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('55', 'Krautiger Bewuchs', 'Krautiger Bewuchs', 'GAF',0.4, 22);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('56', 'Acker mit Kulturen', 'Acker mit Kulturen', 'GAF',0.6, 21);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('57', 'unregelm��iges Vorland', 'unregelm��iges Vorland', 'GAF',0.8, 15);
INSERT INTO roughness (name, label, description, point_kind, k_value, kst_value) VALUES ('58', 'sehr unregelm��iges Vorland mit Verbauungen', 'sehr unregelm��iges Vorland mit Verbauungen', 'GAF',1, 12);

-- 09_prefill_table__vegetation
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('-1', 'unknown', 'unknown','GAF',0,0,0);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('1', 'R�hricht, licht', 'R�hricht, licht', 'GAF',0.003, 0.03, 0.03);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('2', 'R�hricht, dicht', 'R�hricht, dicht', 'GAF',0.005, 0.02, 0.02);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('3', 'Str�ucher, licht', 'Str�ucher, licht', 'GAF',0.03, 0.35, 0.35);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('4', 'Str�ucher, mittel', 'Str�ucher, mittel', 'GAF',0.045, 0.25, 0.25);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('5', 'Str�ucher, dicht', 'Str�ucher, dicht', 'GAF',0.06, 0.15, 0.15);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('6', 'B�ume, licht', 'B�ume, licht', 'GAF',0.05, 5, 5);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('7', 'B�ume, mittel', 'B�ume, mittel', 'GAF',0.2, 10, 10);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('8', 'B�ume, dicht', 'B�ume, dicht', 'GAF',1, 5, 5);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('11', 'B�sche, einj�hrig', 'B�sche, einj�hrig', 'GAF',0.5, 10, 10);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('12', 'B�sche, mehrj�hrig', 'B�sche, mehrj�hrig', 'GAF',3.5, 10, 10);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('13', 'B�ume, einj�hrig', 'B�ume, einj�hrig', 'GAF',0.05, 20, 20);
INSERT INTO vegetation(name, label, description, point_kind, dp, ax, ay) VALUES ('14', 'B�ume, mehrj�hrig', 'B�ume, mehrj�hrig', 'GAF',1, 20, 20);

-- 99_create_spatial_indexes.sql

CREATE INDEX IX_POINT__LOCATION ON "POINT"("LOCATION") INDEXTYPE IS MDSYS.SPATIAL_INDEX ;
CREATE INDEX IX_CROSS_SECTION_PART__LINE ON CROSS_SECTION_PART(LINE) INDEXTYPE IS MDSYS.SPATIAL_INDEX ;
CREATE INDEX IX_CROSS_SECTION__LINE ON CROSS_SECTION(LINE) INDEXTYPE IS MDSYS.SPATIAL_INDEX ;
CREATE INDEX IX_WATER_BODY__RIVERLINE ON WATER_BODY(RIVERLINE) INDEXTYPE IS MDSYS.SPATIAL_INDEX ;
CREATE INDEX IX_WL_FIX__LOCATION ON WATERLEVEL_FIXATION(LOCATION) INDEXTYPE IS MDSYS.SPATIAL_INDEX ;

-- Version endg�ltig setzen
UPDATE INFO set value='0.0.1'  where key ='Version';
commit;

-- update to V0.0.2
-- Version setzen auf "creating" oder "updateing"
UPDATE INFO set value='updateing 0.0.1 to 0.0.2'  where key ='Version';

-- 03_create_tables

-- Generated by Oracle SQL Developer Data Modeler 3.0.0.665
--   at:        2011-07-13 16:56:15 MESZ
--   site:      Oracle Database 10g
--   type:      Oracle Database 10g


CREATE TABLE Document
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (100)  NOT NULL ,
     Location MDSYS.SDO_GEOMETRY ,
     FileName VARCHAR2 (2048)  NOT NULL , 
     MimeType VARCHAR2 (50) ,
     Creation_Date TIMESTAMP (0)  NOT NULL , 
     Editing_Date TIMESTAMP (0)  NOT NULL , 
     Editing_User VARCHAR2 (50)  NOT NULL , 
     Measurement_Date TIMESTAMP (0) , 

--  [�] - Aufnahmerichtung (0-360)
 ShotDirection NUMBER (8,3) CHECK ( ShotDirection BETWEEN 0 AND 360) ,

--  [�] - �ffnungswinkel (0-360)
 ViewAngle NUMBER (8,3) CHECK ( ViewAngle BETWEEN 0 AND 360) ,
     Description VARCHAR2 (255) , 
     Cross_Section_ID NUMBER (20) ,
     Water_Body_ID NUMBER (20) ,
     State_ID NUMBER (20)
    ) 
;



COMMENT ON COLUMN Document.ShotDirection IS '[�] - Aufnahmerichtung (0-360)'
;

COMMENT ON COLUMN Document.ViewAngle IS '[�] - �ffnungswinkel (0-360)'
;

ALTER TABLE Document
    ADD CONSTRAINT "Document PK" PRIMARY KEY ( ID  ) ;

ALTER TABLE Document 
    ADD CONSTRAINT "Document Name UK" UNIQUE ( FileName ) ;


COMMENT ON COLUMN Cross_Section.Station IS '[m]'
;


COMMENT ON COLUMN Event.Type IS 'Art des Ereignisses: Messung, Simulation'
;


COMMENT ON COLUMN Point.Code IS '(GAF-) Kennzeichnung'
;


COMMENT ON COLUMN Point.Width IS '[m]'
;


COMMENT ON COLUMN Point.Height IS '[m NN]'
;


COMMENT ON COLUMN Point.Roughness_K_Value IS '[mm]'
;


COMMENT ON COLUMN Point.Roughness_Kst_Value IS '[m^.33/s]'
;


COMMENT ON COLUMN Point.Vegetation_Dp IS '[m]'
;


COMMENT ON COLUMN Point.Vegetation_Ax IS '[m]'
;


COMMENT ON COLUMN Point.Vegetation_Ay IS '[m]'
;


COMMENT ON COLUMN Point_Kind.Name IS 'Quelle f�r Import: GAF, WPROF, ...'
;


COMMENT ON COLUMN Roughness.K_Value IS '[mm]'
;


COMMENT ON COLUMN Roughness.Kst_Value IS '[m^.33/s]'
;


COMMENT ON COLUMN Roughness.Source IS 'Angabe einer Literaturstelle'
;


COMMENT ON COLUMN State.IsStateZero IS 'Handelt es sich bei diesem Zustand um einen Ur-Zustand (d.h. einen Erstimport)?'
;


COMMENT ON COLUMN State.Source IS 'data source (e.g. file name)'
;


COMMENT ON COLUMN Vegetation.Dp IS '[m]'
;


COMMENT ON COLUMN Vegetation.Ax IS '[m]'
;


COMMENT ON COLUMN Vegetation.Ay IS '[m]'
;


COMMENT ON COLUMN Vegetation.Source IS 'Angabe einer Literaturstelle'
;


COMMENT ON COLUMN Water_Body.Rank IS 'Gew�sserordnung (ggf. zur Darstellung)'
;


COMMENT ON COLUMN Waterlevel_Fixation.Station IS '[m]'
;


COMMENT ON COLUMN Waterlevel_Fixation.Waterlevel IS '[m NN]'
;


COMMENT ON COLUMN Waterlevel_Fixation.Discharge IS '[m�/s]'
;


ALTER TABLE Event DROP CONSTRAINT "Event Name UK" CASCADE
;

DROP INDEX "Event Name UKX"
;

CREATE UNIQUE INDEX "Event Name UKX" ON Event
    (
     Name,
     Water_Body ASC
    )
;

ALTER TABLE Event ADD
    CONSTRAINT "Event Name UK" UNIQUE (
    Name ,
     Water_Body
    ) ;



ALTER TABLE Document
    ADD CONSTRAINT CS_Document FOREIGN KEY
    (
     Cross_Section_ID
    )
    REFERENCES Cross_Section
    (
     ID
    )
    ON DELETE SET NULL
    NOT DEFERRABLE
;


ALTER TABLE Document
    ADD CONSTRAINT State_Document FOREIGN KEY
    (
     State_ID
    )
    REFERENCES State
    (
     ID
    )
    ON DELETE SET NULL
    NOT DEFERRABLE
;


ALTER TABLE Document
    ADD CONSTRAINT WB_Document FOREIGN KEY
    (
     Water_Body_ID
    )
    REFERENCES Water_Body
    (
     ID
    )
    ON DELETE SET NULL
    NOT DEFERRABLE
;


-- Oracle SQL Developer Data Modeler Summary Report:
--
-- CREATE TABLE                             1
-- CREATE INDEX                             0
-- CREATE VIEW                              0
-- ALTER TABLE                              8
-- DROP TABLE                               0
-- DROP INDEX                               0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- ALTER SEQUENCE                           0
-- ALTER MATERIALIZED VIEW                  0
-- DROP VIEW                                0

--
-- ERRORS                                   0
-- WARNINGS                                 0

-- 05_register_geometries
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('DOCUMENT','LOCATION',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);

-- 06_prefill_table_info
INSERT INTO INFO("KEY", "VALUE") VALUES ('DocumentServer', '${DocumentServer}');

-- 99_create_spatial_indexes
CREATE INDEX IX_DOCUMENT__LOCATION ON "DOCUMENT"("LOCATION") INDEXTYPE IS MDSYS.SPATIAL_INDEX ;
-- Version endg�ltig setzen
UPDATE INFO set value='0.0.3'  where key ='Version';
commit;


-- Version 0.0.3
-- Version setzen auf "creating" oder "updateing"
UPDATE INFO set value='updateing 0.0.2 to 0.0.3'  where key ='Version';

GRANT SELECT ON CROSS_SECTION TO PDB_USER;
GRANT SELECT ON CROSS_SECTION_PART TO PDB_USER;
GRANT SELECT ON EVENT TO PDB_USER;
GRANT SELECT ON INFO TO PDB_USER;
GRANT SELECT ON POINT TO PDB_USER;
GRANT SELECT ON POINT_KIND TO PDB_USER;
GRANT SELECT ON ROUGHNESS TO PDB_USER;
GRANT SELECT ON STATE TO PDB_USER;
GRANT SELECT ON VEGETATION TO PDB_USER;
GRANT SELECT ON WATERLEVEL_FIXATION TO PDB_USER;
GRANT SELECT ON WATER_BODY TO PDB_USER;
GRANT SELECT ON DOCUMENT TO PDB_USER;


GRANT DELETE, INSERT, SELECT, UPDATE ON CROSS_SECTION TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON CROSS_SECTION_PART TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON EVENT TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON INFO TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON POINT TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON POINT_KIND TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON ROUGHNESS TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON STATE TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON VEGETATION TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON WATERLEVEL_FIXATION TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON WATER_BODY TO PDB_ADMIN;
GRANT DELETE, INSERT, SELECT, UPDATE ON DOCUMENT TO PDB_ADMIN;

GRANT SELECT ON SEQ_PDB TO PDB_ADMIN;


INSERT INTO INFO("KEY", "VALUE") VALUES ('srsXName', '${srsXName}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMinX', '${srsMinX}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMaxX', '${srsMaxX}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsTolX', '${srsTolX}');

INSERT INTO INFO("KEY", "VALUE") VALUES ('srsYName', '${srsYName}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMinY', '${srsMinY}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMaxY', '${srsMaxY}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsTolY', '${srsTolY}');

INSERT INTO INFO("KEY", "VALUE") VALUES ('srsZName', '${srsZName}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMinZ', '${srsMinZ}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMaxZ', '${srsMaxZ}');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsTolZ', '${srsTolZ}');

-- Version endg�ltig setzen
UPDATE INFO set value='0.0.3'  where key ='Version';
commit;