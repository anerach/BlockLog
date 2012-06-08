CREATE TABLE IF NOT EXISTS `blocklog_reports` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `message` varchar(255) NOT NULL,
  `date` int(11) NOT NULL,
  `seen` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `player` (`player`),
  KEY `type` (`type`,`seen`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;