DROP TABLE IF EXISTS Product;

CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR NOT NULL,
    manufacturer VARCHAR NOT NULL
);

INSERT INTO product(name, manufacturer) VALUES
('Desk', 'Contoso'),
('Chair', 'Contoso'),
('TV', 'Foobar')
;