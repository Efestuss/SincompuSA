-- Borrar el disparador
DROP TRIGGER usuarios_trigger;

-- Borrar la secuencia
DROP SEQUENCE usuarios_seq;
DROP SEQUENCE proveedor_seq;

-- Borrar la tabla
DROP TABLE Usuarios;

-- Borrar la tabla de Proveedor
DROP TABLE Proveedor;
DROP TABLE productos;
DROP TABLE inventario;

-- Crear una secuencia
CREATE SEQUENCE usuarios_seq START WITH 1 INCREMENT BY 1;

-- Crear la tabla para Usuarios
CREATE TABLE Usuarios (
    id INT PRIMARY KEY,
    nombreUsuario VARCHAR(255),
    contrasena VARCHAR(255),
    rol VARCHAR(50)
);

-- Crear un disparador (trigger) para asignar valores a la columna "id" usando la secuencia
CREATE OR REPLACE TRIGGER usuarios_trigger
BEFORE INSERT ON Usuarios
FOR EACH ROW
BEGIN
    :new.id := usuarios_seq.NEXTVAL;
END;

-- Insertar tres usuarios de ejemplo
INSERT INTO Usuarios (nombreUsuario, contrasena, rol) VALUES ('fausto', 'fausto123', 'administrador');
INSERT INTO Usuarios (nombreUsuario, contrasena, rol) VALUES ('juan', 'juan123', 'bodeguero');
INSERT INTO Usuarios (nombreUsuario, contrasena, rol) VALUES ('justin', 'justin123', 'bodeguero');
INSERT INTO Usuarios (nombreUsuario, contrasena, rol) VALUES ('angie', 'angie123', 'bodeguero');
-- Consultar los registros existentes antes de actualizar
SELECT * FROM Usuarios;

-- Actualizar los valores de id
DECLARE
  last_id NUMBER := 0;
BEGIN
  FOR r IN (SELECT id FROM Usuarios ORDER BY id) LOOP
    UPDATE Usuarios SET id = last_id + 1 WHERE id = r.id;
    last_id := last_id + 1;
  END LOOP;
END;

-- Consultar los registros actualizados
SELECT * FROM Usuarios;

-- Crear la tabla para Proveedor
CREATE TABLE Proveedor (
    Id_Proveedor VARCHAR2(20) PRIMARY KEY,
    Nombre VARCHAR2(100),
    Direccion VARCHAR2(100),
    Telefono VARCHAR2(20),
    Email VARCHAR2(100)
);

-- Crear una secuencia para generar el número en incremento
CREATE SEQUENCE proveedor_seq START WITH 1 INCREMENT BY 1;

-- Crear un procedimiento almacenado para añadir un proveedor
CREATE OR REPLACE PROCEDURE InsertarProveedor(
    p_Nombre IN VARCHAR2,
    p_Direccion IN VARCHAR2,
    p_Telefono IN VARCHAR2,
    p_Email IN VARCHAR2
) AS
    v_Id_Proveedor VARCHAR2(20);
BEGIN
    -- Generar el ID del proveedor en el formato deseado
    SELECT '#PROV' || TO_CHAR(proveedor_seq.NEXTVAL, 'FM000')
    INTO v_Id_Proveedor
    FROM dual;

    -- Insertar el proveedor en la tabla
    INSERT INTO Proveedor(Id_Proveedor, Nombre, Direccion, Telefono, Email)
    VALUES (v_Id_Proveedor, p_Nombre, p_Direccion, p_Telefono, p_Email);

    -- Confirmar la inserción
    COMMIT;
END;



-- Crear un procedimiento almacenado para eliminar un proveedor y ajustar la secuencia
CREATE OR REPLACE PROCEDURE EliminarProveedor(
    p_IdProveedor IN VARCHAR2
) AS
BEGIN
    -- Eliminar el proveedor con el ID especificado
    DELETE FROM Proveedor
    WHERE Id_Proveedor = p_IdProveedor;
    --Eliminar Proveedor de la tabla productos
    DELETE FROM Productos
    WHERE Proveedor = p_IdProveedor;
    -- Confirmar la eliminación
    COMMIT;
END;

select *from proveedor
select *from productos

-- Crear un procedimiento almacenado para actualizar un proveedor
CREATE OR REPLACE PROCEDURE ActualizarProveedor(
    p_IdProveedor IN VARCHAR2,
    p_Nombre IN VARCHAR2,
    p_Direccion IN VARCHAR2,
    p_Telefono IN VARCHAR2,
    p_Email IN VARCHAR2
) AS
BEGIN
    -- Actualizar los datos del proveedor en la tabla
    UPDATE Proveedor
    SET Nombre = p_Nombre, Direccion = p_Direccion, Telefono = p_Telefono, Email = p_Email
    WHERE Id_Proveedor = p_IdProveedor;
    
    -- Confirmar la actualización
    COMMIT;
END;


SELECT * FROM Proveedor
ORDER BY TO_NUMBER(SUBSTR(Id_Proveedor, 6)) ASC;

-- Crear una secuencia
CREATE SEQUENCE producto_seq START WITH 1 INCREMENT BY 1;


-- Crear la tabla para Productos
CREATE TABLE Productos (
    Id_Producto VARCHAR2(20) PRIMARY KEY,
    Nombre VARCHAR2(255),
    Cantidad NUMBER,
    Precio NUMBER,
    Categoria VARCHAR2(50),
    Proveedor VARCHAR2(20),
    CONSTRAINT fk_proveedor FOREIGN KEY (Proveedor) REFERENCES Proveedor(Id_Proveedor)
);

-- Crear un procedimiento almacenado para insertar un producto
CREATE OR REPLACE PROCEDURE InsertarProducto(
    p_Nombre IN VARCHAR2,
    p_Cantidad IN NUMBER,
    p_Precio IN NUMBER,
    p_Categoria IN VARCHAR2,
    p_Proveedor IN VARCHAR2
) AS
    v_Id_Producto VARCHAR2(20);
BEGIN
    -- Generar el ID del producto en el formato deseado
    SELECT '#PROD' || TO_CHAR(producto_seq.NEXTVAL, 'FM000')
    INTO v_Id_Producto
    FROM dual;

    -- Insertar el producto en la tabla
    INSERT INTO Productos(Id_Producto, Nombre, Cantidad, Precio, Categoria, Proveedor)
    VALUES (v_Id_Producto, p_Nombre, p_Cantidad, p_Precio, p_Categoria, p_Proveedor);

    -- Confirmar la inserción
    COMMIT;
END;

-- Crear un procedimiento almacenado para modificar un producto
CREATE OR REPLACE PROCEDURE ModificarProducto(
    p_IdProducto IN VARCHAR2,
    p_Nombre IN VARCHAR2,
    p_Cantidad IN NUMBER,
    p_Precio IN NUMBER,
    p_Categoria IN VARCHAR2,
    p_Proveedor IN VARCHAR2
) AS
BEGIN
    -- Actualizar los datos del producto en la tabla
    UPDATE Productos
    SET Nombre = p_Nombre, Cantidad = p_Cantidad, Precio = p_Precio, Categoria = p_Categoria, Proveedor = p_Proveedor
    WHERE Id_Producto = p_IdProducto;
    
    UPDATE Inventario
    SET  Nombre = p_Nombre, Cantidad = p_Cantidad, Precio = p_Precio, Categoria = p_Categoria, Proveedor = p_Proveedor
    Where Id_Producto = p_IdProducto;
    -- Confirmar la actualización
    COMMIT;
END;

-- Crear un procedimiento almacenado para eliminar un producto
CREATE OR REPLACE PROCEDURE EliminarProducto(
    p_IdProducto IN VARCHAR2
) AS
BEGIN
    -- Eliminar el producto del inventario
    DELETE FROM Inventario WHERE Id_Producto = p_IdProducto;
    
    -- Eliminar el producto con el ID especificado
    DELETE FROM Productos WHERE Id_Producto = p_IdProducto;
    
    -- Confirmar la eliminación
    COMMIT;
END;


--Tabla Inventario 

CREATE TABLE Inventario (
    Id_Producto VARCHAR(50) NOT NULL,
    Nombre VARCHAR(100) NOT NULL,
    Cantidad INT NOT NULL,
    Precio NUMBER NOT NULL,
    Categoria VARCHAR(50),
    Proveedor VARCHAR(50),
    Estado VARCHAR(50),
    FOREIGN KEY (Id_Producto) REFERENCES Productos(Id_Producto)
);


--Trigger para que se actualice automaticamente la tabla de inventario
CREATE OR REPLACE TRIGGER Producto_Insertar
AFTER INSERT ON Productos
FOR EACH ROW
BEGIN
  INSERT INTO Inventario (Id_Producto, Nombre, Cantidad, Precio, Categoria, Proveedor, Estado)
  VALUES (:new.Id_Producto, :new.Nombre, :new.Cantidad, :new.Precio, :new.Categoria, :new.Proveedor, NULL);
END;
/

--Procedimiento para actualizar la cantidad en el inventario
CREATE OR REPLACE NONEDITIONABLE PROCEDURE ModificarCantidadInventario(
    p_IdProducto IN VARCHAR2,
    p_NuevaCantidad IN NUMBER
) AS
    v_FechaModificacion DATE;
    v_Estado VARCHAR2(50);
BEGIN
    -- Obtener la fecha y hora actual
    SELECT SYSTIMESTAMP INTO v_FechaModificacion FROM DUAL;

    -- Actualizar la cantidad del producto en el inventario
    UPDATE Inventario
    SET Cantidad = p_NuevaCantidad
    WHERE Id_Producto = p_IdProducto;

    -- Obtener el nuevo estado en función de la cantidad
    SELECT CASE WHEN p_NuevaCantidad > 0 THEN 'EN STOCK' ELSE 'AGOTADO' END
    INTO v_Estado
    FROM DUAL;

    -- Insertar un registro en la tabla Reportes
    INSERT INTO Reportes (Id_Producto, Nombre, Cantidad, Precio, Categoria, Proveedor, Estado, Fecha_Modificacion)
    SELECT Id_Producto, Nombre, Cantidad, Precio, Categoria, Proveedor, v_Estado, v_FechaModificacion
    FROM Inventario
    WHERE Id_Producto = p_IdProducto;

    -- Confirmar la actualización
    COMMIT;
END;
/
select *from usuarios
--Procedimiento para actualizar usuario
CREATE OR REPLACE PROCEDURE ACTUALIZARUSUARIO(
    P_NOMBREUSUARIO IN VARCHAR2,
    P_NUEVONOMBREUSUARIO IN VARCHAR2,
    P_NUEVACONTRASENA IN VARCHAR2
) AS
    EXISTEUSUARIO NUMBER;

BEGIN
    -- Verificar si el nuevo nombre de usuario ya existe
    SELECT COUNT(*)
    INTO EXISTEUSUARIO
    FROM USUARIOS
    WHERE NOMBREUSUARIO = P_NUEVONOMBREUSUARIO AND NOMBREUSUARIO <> P_NOMBREUSUARIO;

    -- Si el nuevo nombre de usuario ya existe, mostrar un mensaje de error
    IF EXISTEUSUARIO > 0 THEN
        DBMS_OUTPUT.PUT_LINE('El nuevo nombre de usuario ya existe. Por favor, elija otro.');
    ELSE
        -- Si no existe, actualizar el usuario
        UPDATE USUARIOS
        SET NOMBREUSUARIO = P_NUEVONOMBREUSUARIO, CONTRASENA = P_NUEVACONTRASENA
        WHERE NOMBREUSUARIO = P_NOMBREUSUARIO;

        DBMS_OUTPUT.PUT_LINE('Usuario actualizado correctamente.');
    END IF;

END ACTUALIZARUSUARIO;
/

-- Crear la tabla Reportes con el formato deseado para Fecha_Modificacion
CREATE TABLE Reportes (
    Id_Producto VARCHAR2(50),
    Nombre VARCHAR2(100),
    Cantidad NUMBER,
    Precio NUMBER,
    Categoria VARCHAR2(50),
    Proveedor VARCHAR2(50),
    Estado VARCHAR2(50),
    Fecha_Modificacion TIMESTAMP
);

-- Crear un formato personalizado para la columna Fecha_Modificacion
CREATE OR REPLACE TRIGGER reportes_formato_fecha
BEFORE INSERT ON Reportes
FOR EACH ROW
BEGIN
    :NEW.Fecha_Modificacion := TO_TIMESTAMP(TO_CHAR(SYSTIMESTAMP, 'DD-MON-YY HH.MI.SS PM'), 'DD-MON-YY HH:MI:SS PM');
END;
/



select *from reportes

-- Crear un procedimiento almacenado para eliminar un bodeguero 
CREATE OR REPLACE PROCEDURE EliminarBodeguero(
    p_IdBodeguero IN VARCHAR2
) AS
BEGIN
    -- Eliminar el proveedor con el ID especificado
    DELETE FROM USUARIOS 
    WHERE Id = p_IdBodeguero;
    -- Confirmar la eliminación
    COMMIT;
END;

select *from REPORTES

Delete from Reportes