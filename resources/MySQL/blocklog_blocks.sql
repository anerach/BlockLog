CREATE TABLE IF NOT EXISTS `blocklog_blocks` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`player` varchar(75) NOT NULL,
	`world` varchar(75) NOT NULL,
	`block_id` int(11) NOT NULL,
	`type` tinyint(1) NOT NULL,
	`rollback_id` tinyint(1) NOT NULL DEFAULT '0',
	`x` double NOT NULL,
	`y` double NOT NULL,
	`z` double NOT NULL,
	`date` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;