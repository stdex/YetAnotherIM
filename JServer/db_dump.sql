delimiter $$

SET NAMES utf8 default collate utf8_unicode_ci;

CREATE DATABASE `yetAIM` DEFAULT CHARACTER SET utf8 default collate utf8_unicode_ci $$

delimiter $$

CREATE TABLE `account` (
  `guid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Global Unique Identifier',
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `psm` varchar(200) DEFAULT NULL,
  `online` int(11) DEFAULT '0',
  PRIMARY KEY (`guid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


delimiter $$

CREATE TABLE `contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `o_guid` int(11) NOT NULL COMMENT 'Owner Global Unique Identifier',
  `c_guid` int(11) NOT NULL COMMENT 'Contact Global Unique Identifier',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


delimiter $$

CREATE TABLE `contact_request` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `o_guid` int(11) NOT NULL COMMENT 'Owner Global Unique Identifier',
  `r_guid` int(11) NOT NULL COMMENT 'Requestor Global Unique Identifier',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


delimiter $$

CREATE TABLE `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `o_guid` int(11) NOT NULL COMMENT 'Owner Global Unique Identifier',
  `r_guid` int(11) NOT NULL COMMENT 'Requestor Global Unique Identifier',
  `message` text NOT NULL COMMENT 'Text of Message',
  `fsg` text NOT NULL COMMENT 'Flag Server Get Message',
  `fcg` text NOT NULL COMMENT 'Flag Client Get Message',
  `datetime` datetime NOT NULL COMMENT 'DateTime',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


delimiter $$

CREATE TABLE `subscribe` (
  `sid` int(11) NOT NULL AUTO_INCREMENT,
  `title` text NOT NULL,
  PRIMARY KEY (`sid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


delimiter $$

CREATE TABLE `subscribe_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sid` int(11) NOT NULL,
  `guid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


delimiter $$

CREATE TABLE `subscribe_messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `o_guid` int(11) NOT NULL COMMENT 'Owner Global Unique Identifier',
  `r_guid` int(11) NOT NULL COMMENT 'Requestor Global Unique Identifier',
  `sid` int(11) NOT NULL COMMENT 'Subscribe Global Unique Identifier',
  `message` text NOT NULL COMMENT 'Text of Message',
  `fsg` text NOT NULL COMMENT 'Flag Server Get Message',
  `fcg` text NOT NULL COMMENT 'Flag Client Get Message',
  `datetime` datetime NOT NULL COMMENT 'DateTime',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 default collate utf8_unicode_ci$$


 
