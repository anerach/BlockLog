CREATE TABLE IF NOT EXISTS `blocklog_undo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rollback` int(11) NOT NULL,
  `player` varchar(255) NOT NULL,
  `date` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rollback` (`rollback`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;