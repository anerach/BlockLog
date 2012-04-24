CREATE TABLE IF NOT EXISTS 'blocklog_rollbacks' (
	'id' INTEGER PRIMARY KEY NOT NULL,
	'player' VARCHAR(75) NOT NULL,
	'world' VARCHAR(75) NOT NULL,
	'param_player' VARCHAR(75) NULL,
	'param_from' VARCHAR(75) NULL,
	'param_until' VARCHAR(75) NULL,
	'param_area' INTEGER NULL,
	'date' INTEGER NOT NULL
);