-- Version setzen auf "creating" oder "updating"
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



INSERT INTO INFO("KEY", "VALUE") VALUES ('srsXName', 'X');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMinX', '4300000.0');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMaxX', '4600000.0');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsTolX', '0.0005');

INSERT INTO INFO("KEY", "VALUE") VALUES ('srsYName', 'Y');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMinY', '5500000.0');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMaxY', '5800000.0');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsTolY', '0.0005');

INSERT INTO INFO("KEY", "VALUE") VALUES ('srsZName', 'Z');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMinZ', '-1000.0');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsMaxZ', '10000.0');
INSERT INTO INFO("KEY", "VALUE") VALUES ('srsTolZ', '0.0005');

-- Version endgültig setzen
UPDATE INFO set value='0.0.3'  where key ='Version';
commit;