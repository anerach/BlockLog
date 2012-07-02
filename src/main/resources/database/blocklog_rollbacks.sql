CREATE TABLE IF NOT EXISTS `blocklog_rollbacks` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `player` varchar(255) NOT NULL,
  `world` varchar(75) NOT NULL,
  `arg_player` varchar(255) NOT NULL,
  `arg_entity` varchar(75) NOT NULL,
  `arg_since` varchar(15) NOT NULL,
  `arg_until` varchar(15) NOT NULL,
  `arg_area` int(11) NOT NULL,
  `arg_delay` varchar(15) NOT NULL,
  `arg_limit` int(11) NOT NULL,
  `date` int(11) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;