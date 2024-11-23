use barretina;
create table `producte`(
    `id_producte` INT(3) NOT NULL PRIMARY KEY auto_increment,
    `nom` VARCHAR(30) NOT NULL,
    `calent` BOOLEAN,
    `preu` DECIMAL(4,2),
    `tipus` varchar(20) 
);
create table `cambrer`(
    `id_cambrer` INT(2) NOT NULL PRIMARY KEY auto_increment,
    `nom` VARCHAR(30) NOT NULL
);
create table `taula`(
    `id_taula` INT(2) NOT NULL PRIMARY KEY auto_increment,
    `id_cambrer` INT(2) ,
    `ocupada` BOOLEAN,
    FOREIGN KEY (`id_cambrer`) REFERENCES `cambrer`(`id_cambrer`)
);
create table `comanda`(
    `id_comanda` INT(3) NOT NULL PRIMARY KEY auto_increment,
    `id_taula` INT(2) NOT NULL ,
    `preu_total` DECIMAL(6,2),
    `estat` ENUM('sense_pagar','efectuant_pagament','pagat') DEFAULT 'sense_pagar',
    `data_comanda` DATETIME,
    FOREIGN KEY (`id_taula`) REFERENCES `taula`(`id_taula`)
);
create table `comanda_producte`(
    `id_comanda` INT(3) NOT NULL,
    `id_producte` INT(2) NOT NULL,
    `quantitat` INT(2) DEFAULT 1,
    `preu_conjunt` DECIMAL(7,2),
    `estat` enum('demanat','preparacio','llest','pagat'),
    `quantitat_pagada` INT(2) DEFAULT 0,
    `preu_restant` DECIMAL(7,2) DEFAULT 0.00,
    PRIMARY KEY (`id_comanda`, `id_producte`),
    FOREIGN KEY (`id_comanda`) REFERENCES `comanda`(`id_comanda`),
    FOREIGN KEY (`id_producte`) REFERENCES `producte`(`id_producte`)
);
