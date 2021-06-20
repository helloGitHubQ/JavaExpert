-- # 店铺表：店铺id、名称、描述
CREATE TABLE IF NOT EXISTS `ssp_stores` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(16) NOT NULL,
    `description` varchar(1024) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;