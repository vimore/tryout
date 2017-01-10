-- 13>Jul 19 23:59:40 138.83.89.170 MSWinEventLog 0       Security        0       Fri Jul 20 3 59 30 2012 4776    Microsoft-Windows-Security-Auditing             Unknown Success Audit   TPAP1CUADCN02.na.dsmain.com     Credential Validation           The computer attempted to validate the credentials for an account.    Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0  Logon Account: svc-sharepoint-prod  Source Workstation: NDCSRV199  Error Code: 0x0 143575560


-- Tuples in square brackets show the frequencies of occurrence, e.g.
-- [(0,10) (1,20)] means the value '0' occurred 10 times, and the value
-- '1' occurred 20 times.

rawdata = LOAD '/securityx/research/vrz-fullsample.txt' USING PigStorage() AS
   (date_ip_type:chararray,  -- 13>Jul 19 23:59:40 138.83.89.170 MSWinEventLog
    unknown_int1:int,        -- 0 [(0,11321830) (,225343)]
    unknown_str1:chararray,  -- Security [(Security,11321830) (,225343) 
    unknown_int2:int,        -- 0 [(0,11316859) (237816,1) (237834,1)... 4972 unique values]
    date_string:chararray,   -- Fri Jul 20 3 59 30 2012
    event_id:int,            -- 4776
    log_source:chararray,    -- Microsoft-Windows-Security-Auditing
    sid:chararray,   -- A userid or other identifier (machine name?)
    groupName:chararray,  -- [(,225343) (Well Known Group,6643912) (Unknown,3755246) (User,922672)]
    unknown_str4:chararray,  -- [(Failure Audit,738290) (,225343) (Success Audit,10583540)]
    source_host:chararray,   -- TPAP1CUADCN02.na.dsmain.com
    event_type:chararray,    -- Credential Validation
    message:chararray,       -- The computer attempted to validate the credentials for an account.    Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0  Logon Account: svc-sharepoint-prod  Source Workstation: NDCSRV199  Error Code: 0x0
    unknown_int3:int);        -- [(,11547173)]
X = LIMIT rawdata 10;
DUMP X;
