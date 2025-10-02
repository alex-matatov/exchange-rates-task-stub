
CREATE SEQUENCE currency_snapshot_id_seq increment by 1;

-- Fixer send around 170 currencies, so we use a higher increment to fetch ids in one call
CREATE SEQUENCE currency_rate_id_seq increment by 200;


CREATE TABLE currency_snapshot (
	id bigint not null primary key,
	snapshot_date date not null,
	currency_code varchar not null,
	version bigint default 0
);
CREATE UNIQUE INDEX uq_currency_snapshot__snapshot_date_and_currency on currency_snapshot (snapshot_date, currency_code);


CREATE TABLE currency_rate (
	id bigint not null primary key,
	currency_code varchar not null,
	rate numeric(19, 5) not null,
	currency_snapshot_ref_id bigint not null constraint fk_currency_rate_to_currency_snapshot references currency_snapshot,
	version bigint default 0
);
CREATE INDEX index_currency_rate_to_currency_snapshot on currency_rate (currency_snapshot_ref_id);
