CREATE TABLE IF NOT EXISTS `{prefix}interactions` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `player` VARCHAR(255) NOT NULL,
  `block` SMALLINT UNSIGNED NOT NULL,
  `world` VARCHAR(75) NOT NULL,
  `x` SMALLINT NOT NULL,
  `y` SMALLINT NOT NULL,
  `z` SMALLINT NOT NULL,
  `date` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `location` (`world`,`x`,`y`,`z`),
  KEY `player` (`player`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;