CREATE TABLE IF NOT EXISTS `blocklog_reports` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`player` varchar(75) NOT NULL,
	`message` varchar(75) NOT NULL,
	`seen` int(1) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;