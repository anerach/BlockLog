CREATE TABLE IF NOT EXISTS `blocklog_undos` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`rollback_id` int(11) NOT NULL,
	`player` varchar(75) NOT NULL,
	`date` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;