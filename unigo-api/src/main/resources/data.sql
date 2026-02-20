-- Tabla usuario (25 pasajeros)
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Administrador', 'admin', 'admin@ejemplo.com', '$2a$10$hash1', 'ADMIN');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Carlos Ruiz', 'carlos.ruiz', 'carlos.ruiz@email.com', '$2a$10$hash2', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('María López', 'maria.lopez', 'maria.lopez@email.com', '$2a$10$hash3', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Diego Fernández', 'diego.fernandez', 'diego.fernandez@email.com', '$2a$10$hash4', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Laura Sánchez', 'laura.sanchez', 'laura.sanchez@email.com', '$2a$10$hash5', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Javier Martín', 'javier.martin', 'javier.martin@email.com', '$2a$10$hash6', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Patricia Gómez', 'patricia.gomez', 'patricia.gomez@email.com', '$2a$10$hash7', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Roberto Díaz', 'roberto.diaz', 'roberto.diaz@email.com', '$2a$10$hash8', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Elena Torres', 'elena.torres', 'elena.torres@email.com', '$2a$10$hash9', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Miguel Ramírez', 'miguel.ramirez', 'miguel.ramirez@email.com', '$2a$10$hash10', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Isabel Castro', 'isabel.castro', 'isabel.castro@email.com', '$2a$10$hash11', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Fernando Moreno', 'fernando.moreno', 'fernando.moreno@email.com', '$2a$10$hash12', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Carmen Navarro', 'carmen.navarro', 'carmen.navarro@email.com', '$2a$10$hash13', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Antonio Jiménez', 'antonio.jimenez', 'antonio.jimenez@email.com', '$2a$10$hash14', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Lucía Romero', 'lucia.romero', 'lucia.romero@email.com', '$2a$10$hash15', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Pedro Vargas', 'pedro.vargas', 'pedro.vargas@email.com', '$2a$10$hash16','USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Rosa Iglesias', 'rosa.iglesias', 'rosa.iglesias@email.com', '$2a$10$hash17', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Manuel Gil', 'manuel.gil', 'manuel.gil@email.com', '$2a$10$hash18', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Teresa Ortiz', 'teresa.ortiz', 'teresa.ortiz@email.com', '$2a$10$hash19', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Ángel Serrano', 'angel.serrano', 'angel.serrano@email.com', '$2a$10$hash20', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Cristina Molina', 'cristina.molina', 'cristina.molina@email.com', '$2a$10$hash21', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Sergio Blanco', 'sergio.blanco', 'sergio.blanco@email.com', '$2a$10$hash22', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Raquel Suárez', 'raquel.suarez', 'raquel.suarez@email.com', '$2a$10$hash23', 'USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Alberto Vega', 'alberto.vega', 'alberto.vega@email.com', '$2a$10$hash24','USER');
INSERT INTO usuario (nombre, username, email, password, rol) VALUES ('Beatriz Méndez', 'beatriz.mendez', 'beatriz.mendez@email.com','$2a$10$hash25','USER');

-- Tabla pasajero (referencias a IDs 1-25)
INSERT INTO pasajero (id_usuario) VALUES (1);
INSERT INTO pasajero (id_usuario) VALUES (2);
INSERT INTO pasajero (id_usuario) VALUES (3);
INSERT INTO pasajero (id_usuario) VALUES (4);
INSERT INTO pasajero (id_usuario) VALUES (5);
INSERT INTO pasajero (id_usuario) VALUES (6);
INSERT INTO pasajero (id_usuario) VALUES (7);
INSERT INTO pasajero (id_usuario) VALUES (8);
INSERT INTO pasajero (id_usuario) VALUES (9);
INSERT INTO pasajero (id_usuario) VALUES (10);
INSERT INTO pasajero (id_usuario) VALUES (11);
INSERT INTO pasajero (id_usuario) VALUES (12);
INSERT INTO pasajero (id_usuario) VALUES (13);
INSERT INTO pasajero (id_usuario) VALUES (14);
INSERT INTO pasajero (id_usuario) VALUES (15);
INSERT INTO pasajero (id_usuario) VALUES (16);
INSERT INTO pasajero (id_usuario) VALUES (17);
INSERT INTO pasajero (id_usuario) VALUES (18);
INSERT INTO pasajero (id_usuario) VALUES (19);
INSERT INTO pasajero (id_usuario) VALUES (20);
INSERT INTO pasajero (id_usuario) VALUES (21);
INSERT INTO pasajero (id_usuario) VALUES (22);
INSERT INTO pasajero (id_usuario) VALUES (23);
INSERT INTO pasajero (id_usuario) VALUES (24);
INSERT INTO pasajero (id_usuario) VALUES (25);

-- Tabla usuario (25 conductores)
INSERT INTO usuario (nombre, username, email, password) VALUES ('Francisco Castro', 'francisco.castro', 'francisco.castro@email.com', '$2a$10$hash26');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Pilar Herrera', 'pilar.herrera', 'pilar.herrera@email.com', '$2a$10$hash27');
INSERT INTO usuario (nombre, username, email, password) VALUES ('José Álvarez', 'jose.alvarez', 'jose.alvarez@email.com', '$2a$10$hash28');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Montserrat Fuentes', 'montserrat.fuentes', 'montserrat.fuentes@email.com', '$2a$10$hash29');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Rubén Campos', 'ruben.campos', 'ruben.campos@email.com', '$2a$10$hash30');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Silvia Ramos', 'silvia.ramos', 'silvia.ramos@email.com', '$2a$10$hash31');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Enrique Cano', 'enrique.cano', 'enrique.cano@email.com', '$2a$10$hash32');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Dolores Prieto', 'dolores.prieto', 'dolores.prieto@email.com', '$2a$10$hash33');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Julio Pascual', 'julio.pascual', 'julio.pascual@email.com', '$2a$10$hash34');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Amparo Santos', 'amparo.santos', 'amparo.santos@email.com', '$2a$10$hash35');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Ramón Lorenzo', 'ramon.lorenzo', 'ramon.lorenzo@email.com', '$2a$10$hash36');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Mercedes Benítez', 'mercedes.benitez', 'mercedes.benitez@email.com', '$2a$10$hash37');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Salvador Hidalgo', 'salvador.hidalgo', 'salvador.hidalgo@email.com', '$2a$10$hash38');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Consuelo León', 'consuelo.leon', 'consuelo.leon@email.com', '$2a$10$hash39');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Tomás Marín', 'tomas.marin', 'tomas.marin@email.com', '$2a$10$hash40');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Josefa Gallego', 'josefa.gallego', 'josefa.gallego@email.com', '$2a$10$hash41');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Joaquín Vidal', 'joaquin.vidal', 'joaquin.vidal@email.com', '$2a$10$hash42');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Encarnación Méndez', 'encarnacion.mendez', 'encarnacion.mendez@email.com', '$2a$10$hash43');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Alfredo Cabrera', 'alfredo.cabrera', 'alfredo.cabrera@email.com', '$2a$10$hash44');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Victoria Sanz', 'victoria.sanz', 'victoria.sanz@email.com', '$2a$10$hash45');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Luis Domínguez', 'luis.dominguez', 'luis.dominguez@email.com', '$2a$10$hash46');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Asunción Reyes', 'asuncion.reyes', 'asuncion.reyes@email.com', '$2a$10$hash47');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Emilio Cruz', 'emilio.cruz', 'emilio.cruz@email.com', '$2a$10$hash48');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Rosario Peña', 'rosario.pena', 'rosario.pena@email.com', '$2a$10$hash49');
INSERT INTO usuario (nombre, username, email, password) VALUES ('Andrés Rojas', 'andres.rojas', 'andres.rojas@email.com', '$2a$10$hash50');

-- Tabla conductor (referencias a IDs 1-25 con reputación)
INSERT INTO conductor (id_usuario, reputacion) VALUES (1, 4.5);
INSERT INTO conductor (id_usuario, reputacion) VALUES (2, 4.8);
INSERT INTO conductor (id_usuario, reputacion) VALUES (3, 4.2);
INSERT INTO conductor (id_usuario, reputacion) VALUES (4, 4.9);
INSERT INTO conductor (id_usuario, reputacion) VALUES (5, 4.3);
INSERT INTO conductor (id_usuario, reputacion) VALUES (6, 4.7);
INSERT INTO conductor (id_usuario, reputacion) VALUES (7, 4.6);
INSERT INTO conductor (id_usuario, reputacion) VALUES (8, 4.4);
INSERT INTO conductor (id_usuario, reputacion) VALUES (9, 4.8);
INSERT INTO conductor (id_usuario, reputacion) VALUES (10, 4.1);
INSERT INTO conductor (id_usuario, reputacion) VALUES (11, 4.5);
INSERT INTO conductor (id_usuario, reputacion) VALUES (12, 4.9);
INSERT INTO conductor (id_usuario, reputacion) VALUES (13, 4.3);
INSERT INTO conductor (id_usuario, reputacion) VALUES (14, 4.6);
INSERT INTO conductor (id_usuario, reputacion) VALUES (15, 4.7);
INSERT INTO conductor (id_usuario, reputacion) VALUES (16, 4.4);
INSERT INTO conductor (id_usuario, reputacion) VALUES (17, 4.8);
INSERT INTO conductor (id_usuario, reputacion) VALUES (18, 4.2);
INSERT INTO conductor (id_usuario, reputacion) VALUES (19, 4.5);
INSERT INTO conductor (id_usuario, reputacion) VALUES (20, 4.9);
INSERT INTO conductor (id_usuario, reputacion) VALUES (21, 4.6);
INSERT INTO conductor (id_usuario, reputacion) VALUES (22, 4.7);
INSERT INTO conductor (id_usuario, reputacion) VALUES (23, 4.5);
INSERT INTO conductor (id_usuario, reputacion) VALUES (24, 4.8);
INSERT INTO conductor (id_usuario, reputacion) VALUES (25, 4.4);

-- 20 conductores con 1 vehículo (conductores 1-20)
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (1, 'Seat', 'Ibiza', 'Blanco', '1234ABC');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (2, 'Renault', 'Clio', 'Rojo', '2345BCD');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (3, 'Volkswagen', 'Golf', 'Negro', '3456CDE');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (4, 'Peugeot', '208', 'Azul', '4567DEF');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (5, 'Ford', 'Fiesta', 'Gris', '5678EFG');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (6, 'Opel', 'Corsa', 'Blanco', '6789FGH');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (7, 'Toyota', 'Yaris', 'Plata', '7890GHI');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (8, 'Citroën', 'C3', 'Verde', '8901HIJ');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (9, 'Nissan', 'Micra', 'Amarillo', '9012IJK');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (10, 'Hyundai', 'i20', 'Negro', '0123JKL');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (11, 'Mazda', '3', 'Rojo', '1234KLM');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (12, 'Kia', 'Rio', 'Blanco', '2345LMN');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (13, 'Honda', 'Civic', 'Azul', '3456MNO');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (14, 'Fiat', '500', 'Beige', '4567NOP');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (15, 'Seat', 'León', 'Gris', '5678OPQ');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (16, 'Volkswagen', 'Polo', 'Negro', '6789PQR');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (17, 'Renault', 'Mégane', 'Blanco', '7890QRS');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (18, 'Peugeot', '308', 'Plata', '8901RST');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (19, 'Audi', 'A3', 'Negro', '9012STU');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (20, 'BMW', 'Serie 1', 'Azul', '0123TUV');

-- 5 conductores con 2 vehículos (conductores 21-25) = 10 vehículos
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (21, 'Mercedes', 'Clase A', 'Plata', '1234UVW');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (21, 'Seat', 'Arona', 'Rojo', '2345VWX');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (22, 'Toyota', 'Corolla', 'Blanco', '3456WXY');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (22, 'Nissan', 'Qashqai', 'Negro', '4567XYZ');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (23, 'Hyundai', 'Tucson', 'Gris', '5678YZA');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (23, 'Kia', 'Sportage', 'Azul', '6789ZAB');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (24, 'Volkswagen', 'Tiguan', 'Blanco', '7890ABC');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (24, 'Ford', 'Kuga', 'Verde', '8901BCD');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (25, 'Peugeot', '3008', 'Negro', '9012CDE');
INSERT INTO vehiculo (id_conductor, marca, modelo, color, matricula)
    VALUES (25, 'Renault', 'Captur', 'Naranja', '0123DEF');
