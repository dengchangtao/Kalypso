
-- Version setzen auf "creating" oder "updating"
UPDATE INFO set value='updateing 0.0.4 to 0.0.5'  where key ='Version';


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: create CS_Part_Param'  where key ='Version';
CREATE TABLE CS_Part_Parameter
    (
     ID NUMBER (20)  NOT NULL ,
     Key VARCHAR2 (50)  NOT NULL ,
     Value VARCHAR2 (255)  NOT NULL ,
     Cross_Section_Part_ID NUMBER (20)  NOT NULL
    ) LOGGING
;


ALTER TABLE CS_Part_Parameter
    ADD CONSTRAINT "CS_Part_Parameter PK" PRIMARY KEY ( ID ,
     Cross_Section_Part_ID  ) ;

ALTER TABLE CS_Part_Parameter
    ADD CONSTRAINT "CS_Part_Parameter UK" UNIQUE ( Key ,
     Cross_Section_Part_ID ) ;



UPDATE INFO set value='updateing 0.0.4 to 0.0.5: create CS_Part_Type'  where key ='Version';
CREATE TABLE CS_Part_Type
    (
     Category VARCHAR2 (50)  NOT NULL ,
     Description VARCHAR2 (255) ,
     Style_Array_ID NUMBER (20)  NOT NULL
    ) LOGGING
;


ALTER TABLE CS_Part_Type
    ADD CONSTRAINT "CS_Part_Type PK" PRIMARY KEY ( Category  ) ;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: create DHM_Index'  where key ='Version';
CREATE TABLE DHM_Index
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (100)  NOT NULL ,
     Location MDSYS.SDO_GEOMETRY  NOT NULL ,
     FileName VARCHAR2 (2048)  NOT NULL ,
     MimeType VARCHAR2 (50)  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User VARCHAR2 (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Source VARCHAR2 (255) ,
     Editor VARCHAR2 (255) ,
     Measurement_Accuracy VARCHAR2 (50) ,
     Description VARCHAR2 (255) ,
     Copyright VARCHAR2 (255)
    ) LOGGING
;



COMMENT ON COLUMN DHM_Index.Source IS 'Source / Author'
;

COMMENT ON COLUMN DHM_Index.Editor IS 'Editor (German: Herausgeber)'
;

ALTER TABLE DHM_Index
    ADD CONSTRAINT "DHM_Index PK" PRIMARY KEY ( ID  ) ;

ALTER TABLE DHM_Index
    ADD CONSTRAINT "DHM_Index UK" UNIQUE ( FileName ) ;




UPDATE INFO set value='updateing 0.0.4 to 0.0.5: refactor Event'  where key ='Version';
ALTER TABLE Event DROP CONSTRAINT Water_Body__Event CASCADE ;

ALTER TABLE Waterlevel_Fixation DROP CONSTRAINT Event__Waterlevel_Fixation CASCADE ;

ALTER TABLE Event DROP CONSTRAINT "Event PK" CASCADE ;

ALTER TABLE Event DROP CONSTRAINT "Event Name UK" CASCADE ;
ALTER TABLE Event RENAME TO bcp_Event
;

CREATE TABLE Event
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (100)  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User VARCHAR2 (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Source VARCHAR2 (255) ,
     Type VARCHAR2 (50) ,
     WL_Type VARCHAR2 (25)  NOT NULL ,
     Description VARCHAR2 (255) ,
     Water_Body NUMBER (20)  NOT NULL ,
     State_ID NUMBER (20) ,
     Style_Array_ID NUMBER (20)
    ) LOGGING
;



COMMENT ON COLUMN Event.Type IS 'Art des Ereignisses: Messung, Simulation'
;

INSERT INTO Event
    (ID , Name , Creation_Date , Editing_Date , Editing_User , Measurement_Date , Source , Type, WL_Type , Description , Water_Body )
SELECT
    ID , Name , Creation_Date , Editing_Date , Editing_User , Measurement_Date , Source , Type, '1D' , Description , Water_Body
FROM
    bcp_Event
;

ALTER TABLE Event
    ADD CONSTRAINT "Event PK" PRIMARY KEY ( ID  ) ;

ALTER TABLE Event
    ADD CONSTRAINT "Event Name UK" UNIQUE ( Name ,
     Water_Body ) ;




UPDATE INFO set value='updateing 0.0.4 to 0.0.5: create Style'  where key ='Version';
CREATE TABLE Style
    (
     ID NUMBER (20)  NOT NULL ,
     Consecutive_Num NUMBER (11)  NOT NULL ,
     Name VARCHAR2 (50)  NOT NULL ,
     Description VARCHAR2 (255) ,
     Style_Array_ID NUMBER (20)  NOT NULL
    ) LOGGING
;


ALTER TABLE Style
    ADD CONSTRAINT "Style PK" PRIMARY KEY ( ID ,
     Style_Array_ID  ) ;

ALTER TABLE Style
    ADD CONSTRAINT "Style Name UK" UNIQUE ( Name ) ;


ALTER TABLE Style
    ADD CONSTRAINT "Style Number in Array UK" UNIQUE ( Consecutive_Num ,
     Style_Array_ID ) ;




UPDATE INFO set value='updateing 0.0.4 to 0.0.5: create Style_Array'  where key ='Version';
CREATE TABLE Style_Array
    (
     ID NUMBER (20)  NOT NULL ,
     Name VARCHAR2 (50)  NOT NULL
    ) LOGGING
;


ALTER TABLE Style_Array
    ADD CONSTRAINT "Style_Array PK" PRIMARY KEY ( ID  ) ;



UPDATE INFO set value='updateing 0.0.4 to 0.0.5: create Style_Param'  where key ='Version';
CREATE TABLE Style_Parameter
    (
     ID NUMBER (20)  NOT NULL ,
     Key VARCHAR2 (50)  NOT NULL ,
     Value VARCHAR2 (255)  NOT NULL ,
     Style_ID NUMBER (20)  NOT NULL ,
     Style_Style_Array_ID NUMBER (20)  NOT NULL
    ) LOGGING
;


ALTER TABLE Style_Parameter
    ADD CONSTRAINT "Style_Parameter PK" PRIMARY KEY ( ID ,
     Style_ID ,
     Style_Style_Array_ID  ) ;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter CS_Part'  where key ='Version';
ALTER TABLE Cross_Section_Part ADD
    (
     Event_ID NUMBER (20)
    )
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter CS_Part_Param'  where key ='Version';
ALTER TABLE CS_Part_Parameter
    ADD CONSTRAINT CS_Part__CS_Part_Param FOREIGN KEY
    (
     Cross_Section_Part_ID
    )
    REFERENCES Cross_Section_Part
    (
     ID
    )
    ON DELETE CASCADE
    NOT DEFERRABLE
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter CS_Part_Type'  where key ='Version';
ALTER TABLE CS_Part_Type
    ADD CONSTRAINT Style_Array__CS_Part_Type FOREIGN KEY
    (
     Style_Array_ID
    )
    REFERENCES Style_Array
    (
     ID
    )
    NOT DEFERRABLE
;
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: insert into Style_Array'  where key ='Version';
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: insert into Style'  where key ='Version';
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: insert into Style_Param'  where key ='Version';
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: insert into CS_Part_Type'  where key ='Version';

UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter CS_Part (2)'  where key ='Version';
ALTER TABLE Cross_Section_Part
    ADD CONSTRAINT CS_Part_Type__CS_Part FOREIGN KEY
    (
     Category
    )
    REFERENCES CS_Part_Type
    (
     Category
    )
    NOT DEFERRABLE
;

UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter CS_Part (3)'  where key ='Version';
ALTER TABLE Cross_Section_Part
    ADD CONSTRAINT Event__CS_Part FOREIGN KEY
    (
     Event_ID
    )
    REFERENCES Event
    (
     ID
    )
    ON DELETE CASCADE
    NOT DEFERRABLE
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter Event'  where key ='Version';
ALTER TABLE Event
    ADD CONSTRAINT State__Event FOREIGN KEY
    (
     State_ID
    )
    REFERENCES State
    (
     ID
    )
    ON DELETE CASCADE
    NOT DEFERRABLE
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter Event (2)'  where key ='Version';
ALTER TABLE Event
    ADD CONSTRAINT Style_Array__Event FOREIGN KEY
    (
     Style_Array_ID
    )
    REFERENCES Style_Array
    (
     ID
    )
    ON DELETE SET NULL
    NOT DEFERRABLE
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter Event (3)'  where key ='Version';
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
    NOT DEFERRABLE
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter Style'  where key ='Version';
ALTER TABLE Style
    ADD CONSTRAINT Style_Array__Style FOREIGN KEY
    (
     Style_Array_ID
    )
    REFERENCES Style_Array
    (
     ID
    )
    ON DELETE CASCADE
    NOT DEFERRABLE
;

UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter Style_Param'  where key ='Version';
ALTER TABLE Style_Parameter
    ADD CONSTRAINT Style__Style_Param FOREIGN KEY
    (
     Style_ID,
     Style_Style_Array_ID
    )
    REFERENCES Style
    (
     ID,
     Style_Array_ID
    )
    ON DELETE CASCADE
    NOT DEFERRABLE
;


UPDATE INFO set value='updateing 0.0.4 to 0.0.5: alter WL_Fix'  where key ='Version';
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
    NOT DEFERRABLE
;
-- insert geometry info
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: insert geometry info'  where key ='Version';

-- register_geometries
-- Oracle
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME,COLUMN_NAME,DIMINFO,SRID)
VALUES('DHM_INDEX','LOCATION',
MDSYS.SDO_DIM_ARRAY
(MDSYS.SDO_DIM_ELEMENT('${srsXName}',${srsMinX},${srsMaxX},${srsTolX}),
MDSYS.SDO_DIM_ELEMENT('${srsYName}',${srsMinY},${srsMaxY},${srsTolY}),
MDSYS.SDO_DIM_ELEMENT('${srsZName}',${srsMinZ},${srsMaxZ},${srsTolZ})
), ${SRID}
);
CREATE INDEX IX_DHM_INDEX__LOCATION ON "DHM_INDEX"("LOCATION") INDEXTYPE IS MDSYS.SPATIAL_INDEX ;


-- PostGis
--INSERT INTO geometry_columns(f_table_catalog, f_table_schema, f_table_name, f_geometry_column, coord_dimension, srid, "type")
--VALUES ( '', 'pdb', 'dhm_index', 'location', 3, ${SRID}, 'POLYGON' );
--CREATE INDEX IX_DHM_INDEX__LOCATION ON DHM_INDEX USING GIST ( LOCATION );



-- drop temp table 'bcp_event'
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: drop temp table ''bcp_event'''  where key ='Version';

-- set root path for DHM files
UPDATE INFO set value='updateing 0.0.4 to 0.0.5: set root path for DHM files (''DHMServer'')'  where key ='Version';
INSERT INTO INFO("KEY", "VALUE") VALUES ('DHMServer', '${DHMServer}');

-- Version endgültig setzen
UPDATE INFO set value='0.0.5'  where key ='Version';
