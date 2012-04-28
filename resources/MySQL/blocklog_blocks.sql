CREATE TABLE IF NOT EXISTS `blocklog_blocks` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`entity` varchar(75) NOT NULL,
	`trigered` varchar(75) NOT NULL,
	`world` varchar(75) NOT NULL,
	`block_id` int(11) NOT NULL,
	`datavalue` int(11) NOT NULL,
	`gamemode` tinyint(1) NOT NULL,
	`type` tinyint(1) NOT NULL,
	`rollback_id` int(11) NOT NULL DEFAULT '0',
	`x` int(11) NOT NULL,
	`y` int(11) NOT NULL,
	`z` int(11) NOT NULL,
	`date` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;