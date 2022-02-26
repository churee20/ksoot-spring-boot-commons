--------------------------------------------------------
--  File created - Thrusday-Jan-28-2021   
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Sequence HIBERNATE_SEQUENCE
--------------------------------------------------------

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence INCREMENT BY 1 START WITH 1000 NO CYCLE;

--------------------------------------------------------
--  DDL for Table revinfo
--------------------------------------------------------

CREATE TABLE revinfo
(
    id integer PRIMARY KEY,
    "timestamp" bigint,
    date_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    actor character varying(255)
);

CREATE INDEX idx_revinfo_date_time ON revinfo (date_time);
