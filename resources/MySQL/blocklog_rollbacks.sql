CREATE TABLE IF NOT EXISTS `blocklog_rollbacks` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`player` varchar(75) NOT NULL,
	`world` varchar(75) NOT NULL,
	`param_player` varchar(75) NULL,
	`param_from` varchar(75) NULL,
	`param_until` varchar(75) NULL,
	`param_area` int(11) NULL,
	`date` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;