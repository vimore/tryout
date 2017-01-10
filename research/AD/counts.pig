-- 13>Jul 19 23:59:40 138.83.89.170 MSWinEventLog 0       Security        0       Fri Jul 20 3 59 30 2012 4776    Microsoft-Windows-Security-Auditing             Unknown Success Audit   TPAP1CUADCN02.na.dsmain.com     Credential Validation           The computer attempted to validate the credentials for an account.    Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0  Logon Account: svc-sharepoint-prod  Source Workstation: NDCSRV199  Error Code: 0x0 143575560



rawdata = LOAD '/securityx/research/vrz-fullsample.txt' USING PigStorage() AS
   (date_ip_type:chararray,  -- 13>Jul 19 23:59:40 138.83.89.170 MSWinEventLog
    unknown_int1:int,        -- 0
    unknown_str1:chararray,  -- Security
    unknown_int2:int,        -- 0
    date_string:chararray,   -- Fri Jul 20 3 59 30 2012
    event_id:int,            -- 4776
    log_source:chararray,    -- Microsoft-Windows-Security-Auditing
    sid:chararray,   -- (empty)
    groupName:chararray,  -- Unknown
    unknown_str4:chararray,  -- Success Audit
    source_host:chararray,   -- TPAP1CUADCN02.na.dsmain.com
    event_type:chararray,    -- Credential Validation
    message:chararray,       -- The computer attempted to validate the credentials for an account.    Authentication Package: MICROSOFT_AUTHENTICATION_PACKAGE_V1_0  Logon Account: svc-sharepoint-prod  Source Workstation: NDCSRV199  Error Code: 0x0
    unknown_int3:int);        -- 143575560

field = FOREACH rawdata GENERATE $field;
counts = GROUP field BY $field;
counts = FOREACH counts GENERATE group AS $field, COUNT_STAR(field) as count:int;
DUMP counts
