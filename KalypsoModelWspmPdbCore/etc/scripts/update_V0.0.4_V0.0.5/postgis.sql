-- Version setzen auf "creating" oder "updating"
UPDATE INFO set value='updating 0.0.4 to 0.0.5'  where key ='Version';


UPDATE INFO set value='updating 0.0.4 to 0.0.5: create CS_Part_Param'  where key ='Version';
CREATE TABLE CS_Part_Parameter
    (
     ID numeric (20)  NOT NULL ,
     Key varchar (50)  NOT NULL ,
     Value varchar (255)  NOT NULL ,
     Cross_Section_Part_ID numeric (20)  NOT NULL
    )
;


ALTER TABLE CS_Part_Parameter
    ADD CONSTRAINT "CS_Part_Parameter PK" PRIMARY KEY ( ID ,
     Cross_Section_Part_ID  ) ;

ALTER TABLE CS_Part_Parameter
    ADD CONSTRAINT "CS_Part_Parameter UK" UNIQUE ( Key ,
     Cross_Section_Part_ID ) ;



UPDATE INFO set value='updating 0.0.4 to 0.0.5: create CS_Part_Type'  where key ='Version';
CREATE TABLE CS_Part_Type
    (
     Category varchar (50)  NOT NULL ,
     Description varchar (255) ,
     Style_Array_ID numeric (20)  NOT NULL
    )
;


ALTER TABLE CS_Part_Type
    ADD CONSTRAINT "CS_Part_Type PK" PRIMARY KEY ( Category  ) ;


UPDATE INFO set value='updating 0.0.4 to 0.0.5: create DHM_Index'  where key ='Version';
CREATE TABLE DHM_Index
    (
     ID numeric (20)  NOT NULL ,
     Name varchar (100)  NOT NULL ,
     Location Geometry  NOT NULL ,
     FileName varchar (2048)  NOT NULL ,
     MimeType varchar (50)  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User varchar (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Source varchar (255) ,
     Editor varchar (255) ,
     Measurement_Accuracy varchar (50) ,
     Description varchar (255) ,
     Copyright varchar (255)
    )
    WITH
    (
    OIDS=TRUE
    )

;



COMMENT ON COLUMN DHM_Index.Source IS 'Source / Author'
;

COMMENT ON COLUMN DHM_Index.Editor IS 'Editor (German: Herausgeber)'
;

ALTER TABLE DHM_Index
    ADD CONSTRAINT "DHM_Index PK" PRIMARY KEY ( ID  ) ;

ALTER TABLE DHM_Index
    ADD CONSTRAINT "DHM_Index UK" UNIQUE ( FileName ) ;




UPDATE INFO set value='updating 0.0.4 to 0.0.5: refactor Event'  where key ='Version';
ALTER TABLE Event DROP CONSTRAINT Water_Body__Event CASCADE ;

ALTER TABLE Waterlevel_Fixation DROP CONSTRAINT Event__Waterlevel_Fixation CASCADE ;

ALTER TABLE Event DROP CONSTRAINT "Event PK" CASCADE ;

ALTER TABLE Event DROP CONSTRAINT "Event Name UK" CASCADE ;
ALTER TABLE Event RENAME TO bcp_Event
;

CREATE TABLE Event
    (
     ID numeric (20)  NOT NULL ,
     Name varchar (100)  NOT NULL ,
     Creation_Date TIMESTAMP (0)  NOT NULL ,
     Editing_Date TIMESTAMP (0)  NOT NULL ,
     Editing_User varchar (50)  NOT NULL ,
     Measurement_Date TIMESTAMP (0) ,
     Source varchar (255) ,
     Type varchar (50) ,
     WL_Type varchar (25)  NOT NULL ,
     Description varchar (255) ,
     Water_Body numeric (20)  NOT NULL ,
     State_ID numeric (20) ,
     Style_Array_ID numeric (20)
    )
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




UPDATE INFO set value='updating 0.0.4 to 0.0.5: create Style'  where key ='Version';
CREATE TABLE Style
    (
     ID numeric (20)  NOT NULL ,
     Consecutive_Num numeric (11)  NOT NULL ,
     Name varchar (50)  NOT NULL ,
     Description varchar (255) ,
     Style_Array_ID numeric (20)  NOT NULL
    )
;


ALTER TABLE Style
    ADD CONSTRAINT "Style PK" PRIMARY KEY ( ID  ) ;

ALTER TABLE Style
    ADD CONSTRAINT "Style Name UK" UNIQUE ( Name ) ;


ALTER TABLE Style
    ADD CONSTRAINT "Style numeric in Array UK" UNIQUE ( Consecutive_Num ,
     Style_Array_ID ) ;




UPDATE INFO set value='updating 0.0.4 to 0.0.5: create Style_Array'  where key ='Version';
CREATE TABLE Style_Array
    (
     ID numeric (20)  NOT NULL ,
     Name varchar (50)  NOT NULL
    )
;


ALTER TABLE Style_Array
    ADD CONSTRAINT "Style_Array PK" PRIMARY KEY ( ID  ) ;



UPDATE INFO set value='updating 0.0.4 to 0.0.5: create Style_Param'  where key ='Version';
CREATE TABLE Style_Parameter
    (
     ID numeric (20)  NOT NULL ,
     Key varchar (50)  NOT NULL ,
     Value varchar (255)  NOT NULL ,
     Style_ID numeric (20)  NOT NULL
    )
;


ALTER TABLE Style_Parameter
    ADD CONSTRAINT "Style_Parameter PK" PRIMARY KEY ( ID  ) ;


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter CS_Part'  where key ='Version';
ALTER TABLE pdb.Cross_Section_Part ADD column Event_ID numeric(20)
;


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter CS_Part_Param'  where key ='Version';
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


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter CS_Part_Type'  where key ='Version';
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
UPDATE INFO set value='updating 0.0.4 to 0.0.5: insert into Style_Array'  where key ='Version';
INSERT INTO style_array (id, name) VALUES (1,'Profil' );
INSERT INTO style_array (id, name) VALUES (2,'Schlammsohle' );
INSERT INTO style_array (id, name) VALUES (3,'Wasserspiegel' );
INSERT INTO style_array (id, name) VALUES (4,'Allgemeiner Bauwerkspunkt' );
INSERT INTO style_array (id, name) VALUES (5,'Bauwerksunterkante' );
INSERT INTO style_array (id, name) VALUES (6,'Durchlass' );
INSERT INTO style_array (id, name) VALUES (7,'Bauwerksoberkante' );

UPDATE INFO set value='updating 0.0.4 to 0.0.5: insert into Style'  where key ='Version';
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (1, 1, 'Profillinie', '', 1 );
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (2, 1, 'Schlammsohlenlinie', '', 2 );
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (3, 1, 'Wasserspiegellinie', '', 3 );
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (4, 1, 'Allgemeiner Bauwerkspunkt', '', 4 );
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (5, 1, 'Bauwerksunterkante (Linie)', '', 5 );
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (6, 1, 'Durchlass (Punkt)', '', 6 );
INSERT INTO style (id, consecutive_num, name, description, style_array_id) VALUES (7, 1, 'Bauwerksoberkante (Linie)', '', 7 );

UPDATE INFO set value='updating 0.0.4 to 0.0.5: insert into Style_Param'  where key ='Version';
INSERT INTO style_parameter (id, key, value, style_id) VALUES (1, 'type', 'line', 1);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (2, 'width', '2', 1);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (3, 'color', '#FF9600', 1);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (4, 'type', 'line', 2);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (5, 'width', '2', 2);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (6, 'color', '#C85F08', 2);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (7, 'type', 'line', 3);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (8, 'width', '2', 3);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (9, 'color', '#0000FF', 3);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (10, 'type', 'point', 4);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (11, 'width', '8', 4);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (12, 'height', '8', 4);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (13, 'fillColor', '#DA9696', 4);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (14, 'strokeWidth', '1', 4);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (15, 'strokeColor', '#000000', 4);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (16, 'type', 'line', 5);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (17, 'width', '2', 5);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (18, 'color', '#0080B3', 5);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (19, 'type', 'point', 6);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (20, 'width', '8', 6);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (21, 'height', '8', 6);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (22, 'fillColor', '#808080', 6);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (23, 'strokeWidth', '1', 6);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (24, 'strokeColor', '#000000', 6);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (25, 'type', 'line', 7);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (26, 'width', '2', 7);
INSERT INTO style_parameter (id, key, value, style_id) VALUES (27, 'color', '#008000', 7);

UPDATE INFO set value='updating 0.0.4 to 0.0.5: insert into CS_Part_Type'  where key ='Version';
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('P', 'Profil', 1);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('S', 'Schlammsohle', 2);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('W', 'Wasserspiegel', 3);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('A', 'Allgemeiner Bauwerkspunkt', 4);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('UK', 'Bauwerksunterkante', 5);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('K', 'Kreisdurchlass', 6);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('EI', 'Ei-Norm-Profil', 6);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('MA', 'Maul-Norm-Profil', 6);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('AR', 'ARMCO71-Profil', 6);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('HA', 'HAMCO84-Profil', 6);
INSERT INTO cs_part_type (category, description, style_array_id) VALUES ('OK', 'Bauwerksoberkante', 7);

UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter CS_Part (2)'  where key ='Version';
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

UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter CS_Part (3)'  where key ='Version';
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


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter Event'  where key ='Version';
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


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter Event (2)'  where key ='Version';
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


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter Event (3)'  where key ='Version';
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


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter Style'  where key ='Version';
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

UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter Style_Param'  where key ='Version';
ALTER TABLE Style_Parameter
    ADD CONSTRAINT Style__Style_Param FOREIGN KEY
    (
     Style_ID
    )
    REFERENCES Style
    (
     ID
    )
    ON DELETE CASCADE
    NOT DEFERRABLE
;


UPDATE INFO set value='updating 0.0.4 to 0.0.5: alter WL_Fix'  where key ='Version';
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
UPDATE INFO set value='updating 0.0.4 to 0.0.5: insert geometry info'  where key ='Version';

-- register_geometries
-- PostGis
INSERT INTO geometry_columns(f_table_catalog, f_table_schema, f_table_name, f_geometry_column, coord_dimension, srid, "type")
VALUES ( '', 'pdb', 'dhm_index', 'location', 3, ${SRID}, 'POLYGON' );
CREATE INDEX IX_DHM_INDEX__LOCATION ON DHM_INDEX USING GIST ( LOCATION );


-- drop temp table 'bcp_event'
UPDATE INFO set value='updating 0.0.4 to 0.0.5: drop temp table ''bcp_event'''  where key ='Version';
DROP TABLE BCP_EVENT;

-- set root path for DHM files
UPDATE INFO set value='updating 0.0.4 to 0.0.5: set root path for DHM files (''DHMServer'')'  where key ='Version';
INSERT INTO INFO("key", "value") VALUES ('DHMServer', '${DHMServer}');

-- Version endgültig setzen
UPDATE INFO set value='0.0.5'  where key ='Version';
