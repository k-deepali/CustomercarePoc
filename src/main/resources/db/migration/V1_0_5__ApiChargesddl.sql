ALTER TABLE `customer_care`.`api_charges`
ADD UNIQUE INDEX `month_UNIQUE` (`month` ASC);
;
UPDATE `customer_care`.`api_charges` SET `month` = '00/0000' WHERE (`id` = '1');
ALTER TABLE `customer_care`.`api_charges`
CHANGE COLUMN `month` `month` VARCHAR(15) NOT NULL ;