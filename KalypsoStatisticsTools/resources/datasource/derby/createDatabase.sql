
CREATE TABLE SysVars (
varname VARCHAR(31) NOT NULL,
varval CLOB NOT NULL,
CONSTRAINT sysvars_primary_key PRIMARY KEY (varname)
);

CREATE TABLE NodeProfile (
UID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
name VARCHAR(2048) NOT NULL,
description VARCHAR(2048) NOT NULL,
profileType VARCHAR(512) NOT NULL DEFAULT 'UNKNOWN',
CONSTRAINT nodeprofile_primary_key PRIMARY KEY (UID)
);

CREATE TABLE TimeSerieProfile (
UID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
nodeProfile_UID INTEGER NOT NULL,
name VARCHAR(2048) NOT NULL,
description VARCHAR(2048) NOT NULL,
profileType VARCHAR(512) NOT NULL DEFAULT 'UNKNOWN',
beginStamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
endStamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
timeStepMillis BIGINT NOT NULL DEFAULT 0,
numberOfEntries INTEGER NOT NULL DEFAULT 0,
CONSTRAINT timeserieprofile_primary_key PRIMARY KEY (UID),
CONSTRAINT timeserieprofile_nodeprofile_foreign_key FOREIGN KEY (nodeProfile_UID) REFERENCES NodeProfile(UID) ON DELETE CASCADE
);

CREATE TABLE TimeSerieEntry (
UID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
timeserieProfile_UID INTEGER NOT NULL,
entryDate BIGINT NOT NULL DEFAULT 0,
entryValue DOUBLE NOT NULL DEFAULT 0.0,
CONSTRAINT timeserieentry_primary_key PRIMARY KEY (UID),
CONSTRAINT timeserieentry_timeserieprofile_foreign_key FOREIGN KEY (timeserieProfile_UID) REFERENCES TimeSerieProfile(UID) ON DELETE CASCADE
);

CREATE TABLE EventsLog (
UID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
logLevel INTEGER NOT NULL DEFAULT 1,
logStamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
description VARCHAR(2047) NOT NULL DEFAULT '',
CONSTRAINT eventslog_primary_key PRIMARY KEY (UID)
);

