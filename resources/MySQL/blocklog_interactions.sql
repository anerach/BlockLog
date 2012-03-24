CREATE TABLE IF NOT EXISTS `blocklog_interactions` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`player` varchar(75) NOT NULL,
	`world` varchar(75) NOT NULL,
	`type` int(11) NOT NULL,
	`x` int(11) NOT NULL,
	`y` int(11) NOT NULL,
	`z` int(11) NOT NULL,
	`date` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;