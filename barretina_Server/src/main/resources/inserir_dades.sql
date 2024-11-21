use barretina;
insert into cambrer(nom) VALUES ("Pepe"),("Juan"),("Pedro");

DELIMITER $$

CREATE PROCEDURE insertTaules (inici INT, final INT)
BEGIN
    DECLARE c INT DEFAULT 0;
    DECLARE idcambrer INT;

    SET c = inici;
    SELECT id_cambrer INTO idcambrer FROM cambrer ORDER BY id_cambrer DESC LIMIT 1;

    WHILE c <= final DO
        INSERT INTO taula (id_cambrer, ocupada) VALUES (idcambrer, FALSE);
        SET c = c + 1;
    END WHILE;
END$$

DELIMITER ;
insert into producte(nom, calent, preu, tipus) values
("Coca Cola",0,1.50,"beguda"),
("Cervesa Corona",0,2.00,"beguda"),
("Fanta de Taronja",0,2.50,"beguda"),
("Aigua mineral",0,1.20,"beguda"),
("Vermut",0,2.80,"beguda"),
("Suc de pinya",0,2.20,"beguda"),
("Cafe",1,1.30,"beguda"),

("Patates braves",1,4.50,"tapa"),
("Croquetes de pernil",1,5.00,"tapa"),
("Calamars a la romana",1,6.50,"tapa"),
("Pernil iberic ",0,12.00,"tapa"),
("Bombes picants",1,5.50,"tapa"),
("Xampinyons a l'allet",1,4.80,"tapa"),

("Crema catalana",0,4.00,"postre"),
("Mel i mato",0,4.50,"postre"),
("Pastis de formatge",0,4.80,"postre"),
("Flam d'ou",0,3.50,"postre"),
("Brownie amb gelat",1,5.00,"postre"),
("Maduixes amb nata",0,3.50,"postre"),

("Amanida catalana",0,7.50,"entrant"),
("Sopa de galets",1,6.00,"entrant"),
("Escalivada",0,6.50,"entrant"),
("Canelons",1,8.50,"entrant"),
("Macarrons a la bolonyesa ",1,3.00,"entrant"),
("Gaspatxo",0,5.00,"entrant");

DELIMITER $$

CREATE TRIGGER actualizar_preu_conjunt
AFTER UPDATE ON comanda_producte
FOR EACH ROW
BEGIN
    IF OLD.quantitat <> NEW.quantitat THEN
        UPDATE comanda_producte
        SET preu_conjunt = (SELECT preu FROM producte WHERE id_producte = NEW.id_producte) * NEW.quantitat
        WHERE id_producte = NEW.id_producte and id_comanda= NEW.id_comanda;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER actualizar_preu_restant
AFTER UPDATE ON comanda_producte
FOR EACH ROW
BEGIN
    IF OLD.quantitat_pagada <> NEW.quantitat_pagada THEN
        IF quantitat = NEW.quantitat_pagada THEN
            UPDATE comanda_producte
            SET preu_restant = (SELECT preu FROM producte WHERE id_producte = NEW.id_producte) * ( OLD.quantitat - NEW.quantitat_pagada),
            estat = 'pagada'
            WHERE id_producte = NEW.id_producte and id_comanda= NEW.id_comanda;

        ELSE
            UPDATE comanda_producte
            SET preu_restant = (SELECT preu FROM producte WHERE id_producte = NEW.id_producte) * ( OLD.quantitat - NEW.quantitat_pagada)
            WHERE id_producte = NEW.id_producte and id_comanda= NEW.id_comanda;
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER actualizar_preu_restant
AFTER UPDATE ON comanda_producte
FOR EACH ROW
BEGIN
    IF OLD.quantitat_pagada <> NEW.quantitat_pagada THEN
        IF NEW.quantitat = NEW.quantitat_pagada THEN
            UPDATE comanda_producte
            SET preu_restant = 0, estat = 'pagada'
            WHERE id_producte = NEW.id_producte and id_comanda= NEW.id_comanda;
        ELSE
            UPDATE comanda_producte
            SET preu_restant = (SELECT preu FROM producte WHERE id_producte = NEW.id_producte) * ( NEW.quantitat - NEW.quantitat_pagada)
            WHERE id_producte = NEW.id_producte and id_comanda= NEW.id_comanda;
        END IF;
    END IF;
END$$

DELIMITER ;

