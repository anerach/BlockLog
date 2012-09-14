CREATE TABLE IF NOT EXISTS `{prefix}rollbacks` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `player` VARCHAR(255) NOT NULL,
  `world` VARCHAR(75) NOT NULL,
  `arg_player` VARCHAR(255) NOT NULL,
  `arg_entity` VARCHAR(75) NOT NULL,
  `arg_since` VARCHAR(15) NOT NULL,
  `arg_until` VARCHAR(15) NOT NULL,
  `arg_area` INT NOT NULL,
  `arg_delay` VARCHAR(15) NOT NULL,
  `arg_limit` INT NOT NULL,
  `date` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;