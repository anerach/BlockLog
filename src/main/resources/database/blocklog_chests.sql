CREATE TABLE IF NOT EXISTS `blocklog_chests` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chest` int(11) NOT NULL,
  `item` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `data` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `chest` (`chest`),
  KEY `item` (`item`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;