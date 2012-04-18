CREATE TABLE IF NOT EXISTS `blocklog_deaths` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`player` varchar(75) NOT NULL,
	`type` int(11) NOT NULL,
	`date` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;