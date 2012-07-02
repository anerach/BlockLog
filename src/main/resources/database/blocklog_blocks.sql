CREATE TABLE IF NOT EXISTS `blocklog_blocks` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `player` varchar(255) NOT NULL,
  `entity` varchar(255) NOT NULL,
  `block` int(11) UNSIGNED NOT NULL,
  `data` int(11) UNSIGNED NOT NULL,
  `type` int(11) UNSIGNED NOT NULL,
  `rollback` int(11) UNSIGNED NOT NULL,
  `world` varchar(75) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  `date` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `player` (`player`,`entity`),
  KEY `location` (`x`,`y`,`z`,`world`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;