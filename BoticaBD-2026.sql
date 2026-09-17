-- ------------------------------------------------------------
-- BOTICADB - ESTRUCTURA ACTUALIZADA (SQL SERVER)
-- 7 Tablas: Cliente, Vendedor, Producto, BoletaVenta, DetalleBoleta, Factura, DetalleFactura
-- Incluye: Vistas, Procedimientos y Datos de prueba
-- ------------------------------------------------------------

-- 0) Crear DB si no existe
if DB_ID('BoticaDB2026') IS NULL
BEGIN
    CREATE DATABASE BoticaDB2026;
END
GO

USE BoticaDB2026;
GO

-- 0.1) Limpiar objetos previos (para re-ejecutar)
-- VISTAS
IF OBJECT_ID('dbo.VW_BoletasDetalladas','V') IS NOT NULL DROP VIEW dbo.VW_BoletasDetalladas;
if OBJECT_ID('dbo.VW_BoletasPorVendedor','V') IS NOT NULL DROP VIEW dbo.VW_BoletasPorVendedor;
IF OBJECT_ID('dbo.VW_ProductosProximosVencer','V') IS NOT NULL DROP VIEW dbo.VW_ProductosProximosVencer;
IF OBJECT_ID('dbo.VW_FacturacionCliente','V') IS NOT NULL DROP VIEW dbo.VW_FacturacionCliente;
IF OBJECT_ID('dbo.VW_ProductosLaboratorio','V') IS NOT NULL DROP VIEW dbo.VW_ProductosLaboratorio;

-- PROCEDURES
IF OBJECT_ID('dbo.SP_RegistrarBoletaCompleta','P') IS NOT NULL DROP PROCEDURE dbo.SP_RegistrarBoletaCompleta;
IF OBJECT_ID('dbo.SP_AgregarDetalleBoleta','P') IS NOT NULL DROP PROCEDURE dbo.SP_AgregarDetalleBoleta;
IF OBJECT_ID('dbo.SP_RegistrarFacturaCompleta','P') IS NOT NULL DROP PROCEDURE dbo.SP_RegistrarFacturaCompleta;
IF OBJECT_ID('dbo.SP_ReporteBoletasRango','P') IS NOT NULL DROP PROCEDURE dbo.SP_ReporteBoletasRango;
IF OBJECT_ID('dbo.SP_ReporteBoletasPorVendedor','P') IS NOT NULL DROP PROCEDURE dbo.SP_ReporteBoletasPorVendedor;
IF OBJECT_ID('dbo.SP_BuscarProductoPorNombre','P') IS NOT NULL DROP PROCEDURE dbo.SP_BuscarProductoPorNombre;
IF OBJECT_ID('dbo.SP_ActualizarPrecioProducto','P') IS NOT NULL DROP PROCEDURE dbo.SP_ActualizarPrecioProducto;
IF OBJECT_ID('dbo.SP_EliminarBoletaCompleta','P') IS NOT NULL DROP PROCEDURE dbo.SP_EliminarBoletaCompleta;
IF OBJECT_ID('dbo.SP_ReporteFacturacionCliente','P') IS NOT NULL DROP PROCEDURE dbo.SP_ReporteFacturacionCliente;
IF OBJECT_ID('dbo.SP_ResumenMensualBoletas','P') IS NOT NULL DROP PROCEDURE dbo.SP_ResumenMensualBoletas;
IF OBJECT_ID('dbo.SP_NoVenderProductoVencido','P') IS NOT NULL DROP PROCEDURE dbo.SP_NoVenderProductoVencido;
IF OBJECT_ID('dbo.SP_VerificarStockAntesBoleta','P') IS NOT NULL DROP PROCEDURE dbo.SP_VerificarStockAntesBoleta;
IF OBJECT_ID('dbo.SP_AutorizarDescuento','P') IS NOT NULL DROP PROCEDURE dbo.SP_AutorizarDescuento;

-- TABLAS (eliminar en orden de FKs)
IF OBJECT_ID('dbo.DetalleBoleta','U') IS NOT NULL DROP TABLE dbo.DetalleBoleta;
IF OBJECT_ID('dbo.DetalleFactura','U') IS NOT NULL DROP TABLE dbo.DetalleFactura;
IF OBJECT_ID('dbo.BoletaVenta','U') IS NOT NULL DROP TABLE dbo.BoletaVenta;
IF OBJECT_ID('dbo.Factura','U') IS NOT NULL DROP TABLE dbo.Factura;
IF OBJECT_ID('dbo.Producto','U') IS NOT NULL DROP TABLE dbo.Producto;
IF OBJECT_ID('dbo.Vendedor','U') IS NOT NULL DROP TABLE dbo.Vendedor;
IF OBJECT_ID('dbo.Cliente','U') IS NOT NULL DROP TABLE dbo.Cliente;
GO

-- 1) TABLAS NUEVAS (7)

-- 1.1 Cliente
CREATE TABLE Cliente (
    ClienteID        INT IDENTITY(1,1) PRIMARY KEY,
    NumeroDoc        VARCHAR(20)  NOT NULL UNIQUE,   -- DNI/RUC/Carnet: único
    TipoDocum        VARCHAR(20)  NOT NULL,          -- DNI/RUC/CE
    ClienteNombre    VARCHAR(150) NOT NULL,
    ClienteDireccion VARCHAR(200) NULL
);


-- 1.2 Vendedor
CREATE TABLE Vendedor (
    VendedorID      INT IDENTITY(1,1) PRIMARY KEY,
    Gmail           VARCHAR(120) NOT NULL UNIQUE,
    VendedorNombre  VARCHAR(150) NOT NULL,
    Direccion       VARCHAR(200) NULL,
    Telefono        VARCHAR(20)  NULL
);


-- 1.3 Producto
CREATE TABLE Producto (
    CodigoProducto       VARCHAR(20)  PRIMARY KEY,
    DescripcionProducto  VARCHAR(150) NOT NULL,
    PrecioUnitario       DECIMAL(10,2) NOT NULL,
    Laboratorio          VARCHAR(80)  NULL,
    Lote                 VARCHAR(30)  NULL,
    FechaVencimiento     DATE         NULL,
    Stock                INT          NOT NULL DEFAULT(0)  -- recomendado para reglas de negocio
);

-- 1.4 BoletaVenta
CREATE TABLE BoletaVenta (
    NroBoleta     VARCHAR(30) PRIMARY KEY,
    Moneda        VARCHAR(10) NOT NULL DEFAULT('PEN'),
    FechaEmision  DATETIME    NOT NULL DEFAULT(GETDATE()),
    TipoPago      VARCHAR(20) NOT NULL,                     -- Efectivo/Tarjeta/Yape/Transf
    ID_Cliente    INT         NOT NULL,
    ID_Vendedor   INT         NOT NULL,
    Total         DECIMAL(12,2) NULL,
    CONSTRAINT FK_Boleta_Cliente  FOREIGN KEY (ID_Cliente)  REFERENCES Cliente(ClienteID),
    CONSTRAINT FK_Boleta_Vendedor FOREIGN KEY (ID_Vendedor) REFERENCES Vendedor(VendedorID)
);


-- 1.5 DetalleBoleta (PK compuesta)
CREATE TABLE DetalleBoleta (
    NroBoleta      VARCHAR(30) NOT NULL,
    CodigoProducto VARCHAR(20) NOT NULL,
    Cantidad       INT         NOT NULL CHECK (Cantidad > 0),
    CONSTRAINT PK_DetalleBoleta PRIMARY KEY (NroBoleta, CodigoProducto),
    CONSTRAINT FK_DetBoleta_Boleta   FOREIGN KEY (NroBoleta)      REFERENCES BoletaVenta(NroBoleta) ON DELETE CASCADE,
    CONSTRAINT FK_DetBoleta_Producto FOREIGN KEY (CodigoProducto) REFERENCES Producto(CodigoProducto)
);
GO

-- 1.6 Factura
CREATE TABLE Factura (
    NroFactura    VARCHAR(30) PRIMARY KEY,
    ID_Cliente    INT         NOT NULL,
    ID_Vendedor   INT         NOT NULL,
    Moneda        VARCHAR(10) NOT NULL DEFAULT('PEN'),
    FechaEmision  DATETIME    NOT NULL DEFAULT(GETDATE()),
    TipoPago      VARCHAR(20) NOT NULL,
    Total         DECIMAL(12,2) NULL,
    CONSTRAINT FK_Factura_Cliente  FOREIGN KEY (ID_Cliente)  REFERENCES Cliente(ClienteID),
    CONSTRAINT FK_Factura_Vendedor FOREIGN KEY (ID_Vendedor) REFERENCES Vendedor(VendedorID)
);

-- 1.7 DetalleFactura (PK compuesta)
CREATE TABLE DetalleFactura (
    NroFactura     VARCHAR(30) NOT NULL,
    CodigoProducto VARCHAR(20) NOT NULL,
    Cantidad       INT         NOT NULL CHECK (Cantidad > 0),
    CONSTRAINT PK_DetalleFactura PRIMARY KEY (NroFactura, CodigoProducto),
    CONSTRAINT FK_DetFactura_Factura  FOREIGN KEY (NroFactura)     REFERENCES Factura(NroFactura) ON DELETE CASCADE,
    CONSTRAINT FK_DetFactura_Producto FOREIGN KEY (CodigoProducto)  REFERENCES Producto(CodigoProducto)
);

-- Índices recomendados
CREATE INDEX IX_Boleta_ID_Cliente  ON BoletaVenta(ID_Cliente);
CREATE INDEX IX_Boleta_ID_Vendedor ON BoletaVenta(ID_Vendedor);
CREATE INDEX IX_Factura_ID_Cliente ON Factura(ID_Cliente);
CREATE INDEX IX_Factura_ID_Vendedor ON Factura(ID_Vendedor);
CREATE INDEX IX_DetBoleta_Prod ON DetalleBoleta(CodigoProducto);
CREATE INDEX IX_DetFactura_Prod ON DetalleFactura(CodigoProducto);
CREATE INDEX IX_Producto_Laboratorio ON Producto(Laboratorio);

-- 2) DATOS DE PRUEBA (>=10)

-- 2.1 Clientes (10)
INSERT INTO Cliente (NumeroDoc, TipoDocum, ClienteNombre, ClienteDireccion) VALUES
('45896321','DNI','María López Huamán','Av. Grau 1234 - Cercado'),
('74125896','DNI','Carlos Pérez Rojas','Jr. Los Olivos 456 - Los Olivos'),
('12365478','DNI','Ana Torres Quispe','Av. La Marina 789 - San Miguel'),
('85296314','DNI','Luis Fernández Castro','Jr. Ayacucho 321 - Miraflores'),
('96385214','DNI','Diana Ramírez Soto','Av. Universitaria 555 - SMP'),
('74123658','DNI','José García Mendoza','Jr. Tacna 120 - Barranco'),
('85274136','DNI','Lucía Vargas Pineda','Av. Arequipa 999 - San Isidro'),
('36985214','DNI','Pedro Castillo Ramos','Jr. Lima 201 - SJL'),
('14785236','DNI','Sofía Medina Rios','Av. Bolivia 45 - Breña'),
('25874163','DNI','Andrés Salazar Ruiz','Jr. Puno 88 - Lince');

-- 2.2 Vendedores (10)
INSERT INTO Vendedor (Gmail, VendedorNombre, Direccion, Telefono) VALUES
('jose.morales@example.com','José Morales','Av. Primavera 101','987111111'),
('rosa.gamarra@example.com','Rosa Gamarra','Jr. Los Cedros 202','987222222'),
('miguel.torres@example.com','Miguel Torres','Av. Brasil 303','987333333'),
('lucero.chavez@example.com','Lucero Chávez','Av. Benavides 404','987444444'),
('anderson.vega@example.com','Anderson Vega','Av. La Molina 505','987555555'),
('carolina.ruiz@example.com','Carolina Ruiz','Jr. Huamanga 606','987666666'),
('bruno.hidalgo@example.com','Bruno Hidalgo','Av. Colonial 707','987777777'),
('patricia.salas@example.com','Patricia Salas','Jr. Zorritos 808','987888888'),
('oscar.medina@example.com','Oscar Medina','Av. Brasil 909','987999999'),
('romina.flores@example.com','Romina Flores','Jr. San Martín 010','986000000');

-- 2.3 Productos (12)
INSERT INTO Producto (CodigoProducto, DescripcionProducto, PrecioUnitario, Laboratorio, Lote, FechaVencimiento, Stock) VALUES
('P001','Paracetamol 500mg Caja x10',5.50,'Genfar','L001','2026-05-01',100),
('P002','Amoxicilina 500mg Blister x12',12.00,'Roche','L002','2025-11-10',80),
('P003','Ibuprofeno 400mg Caja x20',8.90,'Bayer','L003','2027-03-15',120),
('P004','Omeprazol 20mg Caja x14',15.00,'Sanofi','L004','2026-09-20',60),
('P005','Vitamina C 1g Caja x30',22.00,'MK','L005','2027-01-30',40),
('P006','Lorazepam 1mg Blister x20',18.50,'LaboA','L006','2023-12-31',10), -- vencido
('P007','Metformina 850mg Caja x30',25.00,'LaboB','L007','2026-07-10',200),
('P008','Cetirizina 10mg Caja x10',7.00,'LaboC','L008','2026-02-28',150),
('P009','Aspirina 100mg Caja x20',4.00,'LaboD','L009','2025-10-10',90),
('P010','Dipirona 500mg Caja x10',6.50,'LaboE','L010','2025-05-05',30),
('P011','Multivitamínico A-Z Caja x30',30.00,'VitaLab','L011','2028-01-01',70),
('P012','Crema Antiséptica 20g',12.50,'DermLab','L012','2026-12-31',55);


-- 2.4 Boletas (10)
INSERT INTO BoletaVenta (NroBoleta, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor, Total) VALUES
('B2025001','PEN','2025-01-10','Efectivo',1,1,55.00),
('B2025002','PEN','2025-02-05','Tarjeta',2,2,120.00),
('B2025003','PEN','2025-03-12','Efectivo',3,3,30.00),
('B2025004','PEN','2025-04-22','Yape',4,4,75.50),
('B2025005','PEN','2025-05-01','Efectivo',5,5,22.00),
('B2025006','PEN','2025-06-15','Tarjeta',6,6,48.90),
('B2025007','PEN','2025-07-01','Yape',7,7,150.00),
('B2025008','PEN','2025-07-20','Efectivo',8,8,9.00),
('B2025009','PEN','2025-08-01','Tarjeta',9,9,40.00),
('B2025010','PEN','2025-08-10','Efectivo',10,10,65.50);


-- 2.5 DetalleBoleta
INSERT INTO DetalleBoleta (NroBoleta, CodigoProducto, Cantidad) VALUES
('B2025001','P001',2),
('B2025001','P011',1),
('B2025002','P002',5),
('B2025002','P007',2),
('B2025003','P003',3),
('B2025004','P005',1),
('B2025004','P004',1),
('B2025005','P005',1),
('B2025006','P009',2),
('B2025006','P008',1),
('B2025007','P007',6),
('B2025008','P010',1),
('B2025009','P001',4),
('B2025010','P012',2),
('B2025010','P003',1);


-- Restar stock según boletas de prueba
UPDATE P SET P.Stock = P.Stock - DB.Cantidad
FROM Producto P
JOIN DetalleBoleta DB ON DB.CodigoProducto = P.CodigoProducto;


-- 2.6 Facturas (10)
INSERT INTO Factura (NroFactura, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor, Total) VALUES
('F2025001','PEN','2025-01-11','Tarjeta',1,1,55.00),
('F2025002','PEN','2025-02-06','Efectivo',2,2,120.00),
('F2025003','PEN','2025-03-13','Efectivo',3,3,30.00),
('F2025004','PEN','2025-04-23','Yape',4,4,75.50),
('F2025005','PEN','2025-05-02','Efectivo',5,5,22.00),
('F2025006','PEN','2025-06-16','Tarjeta',6,6,48.90),
('F2025007','PEN','2025-07-02','Yape',7,7,150.00),
('F2025008','PEN','2025-07-21','Efectivo',8,8,9.00),
('F2025009','PEN','2025-08-02','Tarjeta',9,9,40.00),
('F2025010','PEN','2025-08-11','Efectivo',10,10,65.50);


-- 2.7 DetalleFactura
INSERT INTO DetalleFactura (NroFactura, CodigoProducto, Cantidad) VALUES
('F2025001','P001',2),
('F2025001','P011',1),
('F2025002','P002',5),
('F2025002','P007',2),
('F2025003','P003',3),
('F2025004','P005',1),
('F2025004','P004',1),
('F2025005','P005',1),
('F2025006','P009',2),
('F2025006','P008',1),
('F2025007','P007',6),
('F2025008','P010',1),
('F2025009','P001',4),
('F2025010','P012',2),
('F2025010','P003',1);


-- Restar stock según facturas de prueba
UPDATE P SET P.Stock = P.Stock - DF.Cantidad
FROM Producto P
JOIN DetalleFactura DF ON DF.CodigoProducto = P.CodigoProducto;
go


-- 3) VISTAS (5)

-- 3.1 Boletas detalladas (usa precio actual del producto para SubTotal)
CREATE VIEW VW_BoletasDetalladas AS
SELECT
    B.NroBoleta, B.FechaEmision, B.Moneda, B.TipoPago,
    C.ClienteID, C.ClienteNombre, C.NumeroDoc, C.TipoDocum,
    V.VendedorID, V.VendedorNombre,
    P.CodigoProducto, P.DescripcionProducto, DB.Cantidad,
    P.PrecioUnitario,
    CAST(DB.Cantidad * P.PrecioUnitario AS DECIMAL(12,2)) AS SubTotal
FROM BoletaVenta B
JOIN Cliente C        ON B.ID_Cliente  = C.ClienteID
JOIN Vendedor V       ON B.ID_Vendedor = V.VendedorID
LEFT JOIN DetalleBoleta DB ON B.NroBoleta = DB.NroBoleta
LEFT JOIN Producto P       ON DB.CodigoProducto = P.CodigoProducto;
GO


-- 3.2 Boletas por Vendedor
CREATE VIEW VW_BoletasPorVendedor AS
SELECT
    V.VendedorID, V.VendedorNombre,
    B.NroBoleta, B.FechaEmision,
    SUM(DB.Cantidad * P.PrecioUnitario) AS TotalBoleta
FROM Vendedor V
JOIN BoletaVenta B    ON V.VendedorID = B.ID_Vendedor
JOIN DetalleBoleta DB ON B.NroBoleta = DB.NroBoleta
JOIN Producto P       ON DB.CodigoProducto = P.CodigoProducto
GROUP BY V.VendedorID, V.VendedorNombre, B.NroBoleta, B.FechaEmision;
GO

-- 3.3 Productos próximos a vencer (<= 1 año)
CREATE VIEW VW_ProductosProximosVencer AS
SELECT
    CodigoProducto, DescripcionProducto, FechaVencimiento, Stock,
    DATEDIFF(DAY, GETDATE(), FechaVencimiento) AS DiasParaVencer
FROM Producto
WHERE FechaVencimiento IS NOT NULL
  AND FechaVencimiento <= DATEADD(YEAR,1,GETDATE());
GO

-- 3.4 Facturación por Cliente (usa precio actual)
CREATE VIEW VW_FacturacionCliente AS
SELECT
    F.NroFactura, F.FechaEmision, C.ClienteID, C.ClienteNombre,
    SUM(DF.Cantidad * P.PrecioUnitario) AS TotalFacturado
FROM Factura F
JOIN Cliente C         ON F.ID_Cliente = C.ClienteID
JOIN DetalleFactura DF ON F.NroFactura = DF.NroFactura
JOIN Producto P        ON DF.CodigoProducto = P.CodigoProducto
GROUP BY F.NroFactura, F.FechaEmision, C.ClienteID, C.ClienteNombre;
GO

-- 3.5 Resumen por laboratorio (ventas por boleta)
CREATE VIEW VW_ProductosLaboratorio AS
SELECT
    P.Laboratorio,
    P.CodigoProducto,
    P.DescripcionProducto,
    P.PrecioUnitario,
    SUM(COALESCE(DB.Cantidad,0)) AS CantidadVendida
FROM Producto P
LEFT JOIN DetalleBoleta DB ON P.CodigoProducto = DB.CodigoProducto
GROUP BY P.Laboratorio, P.CodigoProducto, P.DescripcionProducto, P.PrecioUnitario;
GO

-- 4) PROCEDIMIENTOS (13)

-- 4.1 Registrar boleta completa (Boleta + Detalle + stock + total)
CREATE PROCEDURE SP_RegistrarBoletaCompleta
    @NroBoleta     VARCHAR(30),
    @Moneda        VARCHAR(10),
    @FechaEmision  DATETIME,
    @TipoPago      VARCHAR(20),
    @ID_Cliente    INT,
    @ID_Vendedor   INT,
    @CodigoProducto VARCHAR(20),
    @Cantidad       INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRAN;
    BEGIN TRY
        -- No vender vencidos
        IF EXISTS (SELECT 1 FROM Producto WHERE CodigoProducto=@CodigoProducto AND FechaVencimiento < CAST(GETDATE() AS DATE))
        BEGIN
            RAISERROR('No se puede vender producto vencido.',16,1);
            ROLLBACK TRAN; RETURN;
        END
        -- Stock suficiente
        IF (SELECT Stock FROM Producto WHERE CodigoProducto=@CodigoProducto) < @Cantidad
        BEGIN
            RAISERROR('Stock insuficiente para %s.',16,1,@CodigoProducto);
            ROLLBACK TRAN; RETURN;
        END

        INSERT INTO BoletaVenta (NroBoleta, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor)
        VALUES (@NroBoleta, @Moneda, @FechaEmision, @TipoPago, @ID_Cliente, @ID_Vendedor);

        INSERT INTO DetalleBoleta (NroBoleta, CodigoProducto, Cantidad)
        VALUES (@NroBoleta, @CodigoProducto, @Cantidad);

        UPDATE Producto SET Stock = Stock - @Cantidad WHERE CodigoProducto=@CodigoProducto;

        UPDATE B
        SET Total = S.Tot
        FROM BoletaVenta B
        CROSS APPLY (
            SELECT SUM(DB.Cantidad * P.PrecioUnitario) AS Tot
            FROM DetalleBoleta DB
            JOIN Producto P ON P.CodigoProducto = DB.CodigoProducto
            WHERE DB.NroBoleta = B.NroBoleta
        ) S
        WHERE B.NroBoleta = @NroBoleta;

        COMMIT TRAN;
        SELECT @NroBoleta AS NroBoleta;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRAN;
        DECLARE @Err NVARCHAR(4000)=ERROR_MESSAGE();
        RAISERROR('Error SP_RegistrarBoletaCompleta: %s',16,1,@Err);
    END CATCH
END;
GO

-- 4.2 Agregar detalle a boleta
CREATE PROCEDURE SP_AgregarDetalleBoleta
    @NroBoleta      VARCHAR(30),
    @CodigoProducto VARCHAR(20),
    @Cantidad       INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRAN;
    BEGIN TRY
        IF NOT EXISTS (SELECT 1 FROM BoletaVenta WHERE NroBoleta=@NroBoleta)
        BEGIN RAISERROR('Boleta no existe.',16,1); ROLLBACK TRAN; RETURN; END

        IF EXISTS (SELECT 1 FROM Producto WHERE CodigoProducto=@CodigoProducto AND FechaVencimiento < CAST(GETDATE() AS DATE))
        BEGIN RAISERROR('Producto vencido.',16,1); ROLLBACK TRAN; RETURN; END

        IF (SELECT Stock FROM Producto WHERE CodigoProducto=@CodigoProducto) < @Cantidad
        BEGIN RAISERROR('Stock insuficiente.',16,1); ROLLBACK TRAN; RETURN; END

        MERGE DetalleBoleta AS T
        USING (SELECT @NroBoleta AS NroBoleta, @CodigoProducto AS CodigoProducto, @Cantidad AS Cantidad) AS S
        ON (T.NroBoleta = S.NroBoleta AND T.CodigoProducto = S.CodigoProducto)
        WHEN MATCHED THEN UPDATE SET T.Cantidad = T.Cantidad + S.Cantidad
        WHEN NOT MATCHED THEN INSERT (NroBoleta, CodigoProducto, Cantidad) VALUES (S.NroBoleta, S.CodigoProducto, S.Cantidad);

        UPDATE Producto SET Stock = Stock - @Cantidad WHERE CodigoProducto=@CodigoProducto;

        UPDATE B
        SET Total = S.Tot
        FROM BoletaVenta B
        CROSS APPLY (
            SELECT SUM(DB.Cantidad * P.PrecioUnitario) AS Tot
            FROM DetalleBoleta DB
            JOIN Producto P ON P.CodigoProducto = DB.CodigoProducto
            WHERE DB.NroBoleta = B.NroBoleta
        ) S
        WHERE B.NroBoleta = @NroBoleta;

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRAN;
        DECLARE @Err NVARCHAR(4000)=ERROR_MESSAGE();
        RAISERROR('Error SP_AgregarDetalleBoleta: %s',16,1,@Err);
    END CATCH
END;
GO

-- 4.3 Registrar factura completa
CREATE PROCEDURE SP_RegistrarFacturaCompleta
    @NroFactura     VARCHAR(30),
    @Moneda         VARCHAR(10),
    @FechaEmision   DATETIME,
    @TipoPago       VARCHAR(20),
    @ID_Cliente     INT,
    @ID_Vendedor    INT,
    @CodigoProducto VARCHAR(20),
    @Cantidad       INT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRAN;
    BEGIN TRY
        IF EXISTS (SELECT 1 FROM Producto WHERE CodigoProducto=@CodigoProducto AND FechaVencimiento < CAST(GETDATE() AS DATE))
        BEGIN RAISERROR('No se puede facturar producto vencido.',16,1); ROLLBACK TRAN; RETURN; END

        IF (SELECT Stock FROM Producto WHERE CodigoProducto=@CodigoProducto) < @Cantidad
        BEGIN RAISERROR('Stock insuficiente.',16,1); ROLLBACK TRAN; RETURN; END

        INSERT INTO Factura (NroFactura, Moneda, FechaEmision, TipoPago, ID_Cliente, ID_Vendedor)
        VALUES (@NroFactura, @Moneda, @FechaEmision, @TipoPago, @ID_Cliente, @ID_Vendedor);

        INSERT INTO DetalleFactura (NroFactura, CodigoProducto, Cantidad)
        VALUES (@NroFactura, @CodigoProducto, @Cantidad);

        UPDATE Producto SET Stock = Stock - @Cantidad WHERE CodigoProducto=@CodigoProducto;

        UPDATE F
        SET Total = S.Tot
        FROM Factura F
        CROSS APPLY (
            SELECT SUM(DF.Cantidad * P.PrecioUnitario) AS Tot
            FROM DetalleFactura DF
            JOIN Producto P ON P.CodigoProducto = DF.CodigoProducto
            WHERE DF.NroFactura = F.NroFactura
        ) S
        WHERE F.NroFactura = @NroFactura;

        COMMIT TRAN;
        SELECT @NroFactura AS NroFactura;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRAN;
        DECLARE @Err NVARCHAR(4000)=ERROR_MESSAGE();
        RAISERROR('Error SP_RegistrarFacturaCompleta: %s',16,1,@Err);
    END CATCH
END;
GO

-- 4.4 Reporte: Boletas en rango
CREATE PROCEDURE SP_ReporteBoletasRango
    @FechaInicio DATETIME,
    @FechaFin    DATETIME
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        B.NroBoleta, B.FechaEmision, C.ClienteNombre, V.VendedorNombre,
        SUM(DB.Cantidad * P.PrecioUnitario) AS TotalBoleta
    FROM BoletaVenta B
    JOIN Cliente C         ON B.ID_Cliente = C.ClienteID
    JOIN Vendedor V        ON B.ID_Vendedor = V.VendedorID
    JOIN DetalleBoleta DB  ON B.NroBoleta  = DB.NroBoleta
    JOIN Producto P        ON DB.CodigoProducto = P.CodigoProducto
    WHERE B.FechaEmision BETWEEN @FechaInicio AND @FechaFin
    GROUP BY B.NroBoleta, B.FechaEmision, C.ClienteNombre, V.VendedorNombre
    ORDER BY B.FechaEmision;
END;
GO

-- 4.5 Reporte: Boletas por vendedor
CREATE PROCEDURE SP_ReporteBoletasPorVendedor
    @ID_Vendedor INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        V.VendedorID, V.VendedorNombre,
        B.NroBoleta, B.FechaEmision,
        SUM(DB.Cantidad * P.PrecioUnitario) AS TotalBoleta
    FROM Vendedor V
    JOIN BoletaVenta B    ON V.VendedorID = B.ID_Vendedor
    JOIN DetalleBoleta DB ON B.NroBoleta  = DB.NroBoleta
    JOIN Producto P       ON DB.CodigoProducto = P.CodigoProducto
    WHERE V.VendedorID = @ID_Vendedor
    GROUP BY V.VendedorID, V.VendedorNombre, B.NroBoleta, B.FechaEmision
    ORDER BY B.FechaEmision;
END;
GO

-- 4.6 Buscar producto por nombre
CREATE PROCEDURE SP_BuscarProductoPorNombre
    @Nombre VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        P.CodigoProducto, P.DescripcionProducto, P.PrecioUnitario, P.Stock,
        B.NroBoleta, DB.Cantidad AS CantidadVendida
    FROM Producto P
    LEFT JOIN DetalleBoleta DB ON P.CodigoProducto = DB.CodigoProducto
    LEFT JOIN BoletaVenta B    ON DB.NroBoleta = B.NroBoleta
    WHERE P.DescripcionProducto LIKE '%' + @Nombre + '%';
END;
GO

-- 4.7 Actualizar precio de producto
CREATE PROCEDURE SP_ActualizarPrecioProducto
    @CodigoProducto VARCHAR(20),
    @NuevoPrecio    DECIMAL(10,2)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRAN;
    BEGIN TRY
        IF NOT EXISTS (SELECT 1 FROM Producto WHERE CodigoProducto=@CodigoProducto)
        BEGIN RAISERROR('Producto no existe.',16,1); ROLLBACK TRAN; RETURN; END

        UPDATE Producto SET PrecioUnitario=@NuevoPrecio WHERE CodigoProducto=@CodigoProducto;

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRAN;
        DECLARE @Err NVARCHAR(4000)=ERROR_MESSAGE();
        RAISERROR('Error SP_ActualizarPrecioProducto: %s',16,1,@Err);
    END CATCH
END;
GO

-- 4.8 Eliminar boleta completa (restituye stock)
CREATE PROCEDURE SP_EliminarBoletaCompleta
    @NroBoleta VARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRAN;
    BEGIN TRY
        -- Restituir stock
        UPDATE P
        SET P.Stock = P.Stock + DB.Cantidad
        FROM Producto P
        INNER JOIN DetalleBoleta DB ON P.CodigoProducto = DB.CodigoProducto
        WHERE DB.NroBoleta = @NroBoleta;

        DELETE FROM DetalleBoleta WHERE NroBoleta=@NroBoleta;
        DELETE FROM BoletaVenta  WHERE NroBoleta=@NroBoleta;

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRAN;
        DECLARE @Err NVARCHAR(4000)=ERROR_MESSAGE();
        RAISERROR('Error SP_EliminarBoletaCompleta: %s',16,1,@Err);
    END CATCH
END;
GO

-- 4.9 Reporte: Facturación por cliente (detalle)
CREATE PROCEDURE SP_ReporteFacturacionCliente
    @ID_Cliente INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        F.NroFactura, F.FechaEmision, C.ClienteNombre,
        DF.CodigoProducto, P.DescripcionProducto, DF.Cantidad,
        P.PrecioUnitario,
        CAST(DF.Cantidad * P.PrecioUnitario AS DECIMAL(12,2)) AS SubTotal
    FROM Factura F
    JOIN Cliente C         ON F.ID_Cliente = C.ClienteID
    JOIN DetalleFactura DF ON F.NroFactura = DF.NroFactura
    JOIN Producto P        ON DF.CodigoProducto = P.CodigoProducto
    WHERE F.ID_Cliente = @ID_Cliente
    ORDER BY F.FechaEmision;
END;
GO

-- 4.10 Resumen mensual de boletas
CREATE PROCEDURE SP_ResumenMensualBoletas
    @Anio INT,
    @Mes  INT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT
        P.CodigoProducto, P.DescripcionProducto,
        SUM(DB.Cantidad) AS CantidadVendida,
        SUM(DB.Cantidad * P.PrecioUnitario) AS Ingresos
    FROM DetalleBoleta DB
    JOIN BoletaVenta B ON DB.NroBoleta = B.NroBoleta
    JOIN Producto P    ON DB.CodigoProducto = P.CodigoProducto
    WHERE YEAR(B.FechaEmision) = @Anio AND MONTH(B.FechaEmision) = @Mes
    GROUP BY P.CodigoProducto, P.DescripcionProducto
    ORDER BY Ingresos DESC;
END;
GO

-- 4.11 Regla: no vender producto vencido (consulta)
CREATE PROCEDURE SP_NoVenderProductoVencido
    @CodigoProducto VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS (SELECT 1 FROM Producto WHERE CodigoProducto=@CodigoProducto AND FechaVencimiento < CAST(GETDATE() AS DATE))
        SELECT 1 AS Vencido, 'Producto vencido' AS Mensaje;
    ELSE
        SELECT 0 AS Vencido, 'Producto válido para venta' AS Mensaje;
END;
GO

-- 4.12 Verificar stock antes de boleta
CREATE PROCEDURE SP_VerificarStockAntesBoleta
    @CodigoProducto VARCHAR(20),
    @Cantidad       INT
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Stock INT = (SELECT Stock FROM Producto WHERE CodigoProducto=@CodigoProducto);
    IF @Stock IS NULL BEGIN RAISERROR('Producto no existe.',16,1); RETURN; END
    IF @Stock < @Cantidad
        SELECT 0 AS Disponible, @Stock AS StockActual, 'Stock insuficiente' AS Mensaje;
    ELSE
        SELECT 1 AS Disponible, @Stock AS StockActual, 'Stock suficiente' AS Mensaje;
END;
GO

-- 4.13 Autorizar descuento (solo ciertos vendedores por nombre de rol)
-- Para el demo usaremos nombres que incluyan 'Líder' o 'Gerente' (puedes cambiar por una tabla de roles)
CREATE PROCEDURE SP_AutorizarDescuento
    @ID_Vendedor  INT,
    @NroBoleta    VARCHAR(30),
    @DescuentoPorc DECIMAL(5,2)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Nombre VARCHAR(150) = (SELECT VendedorNombre FROM Vendedor WHERE VendedorID=@ID_Vendedor);
    IF @Nombre IS NULL BEGIN RAISERROR('Vendedor no encontrado',16,1); RETURN; END
    IF @Nombre NOT LIKE '%Líder%' AND @Nombre NOT LIKE '%Gerente%'
    BEGIN
        RAISERROR('Vendedor no autorizado para descuentos',16,1); RETURN;
    END

    BEGIN TRAN;
    BEGIN TRY
        DECLARE @TotalActual DECIMAL(12,2) = (
            SELECT SUM(DB.Cantidad * P.PrecioUnitario)
            FROM DetalleBoleta DB JOIN Producto P ON P.CodigoProducto=DB.CodigoProducto
            WHERE DB.NroBoleta=@NroBoleta
        );
        IF @TotalActual IS NULL BEGIN RAISERROR('Boleta sin detalle o inexistente.',16,1); ROLLBACK TRAN; RETURN; END

        DECLARE @NuevoTotal DECIMAL(12,2) = @TotalActual - (@TotalActual * (@DescuentoPorc/100.0));
        UPDATE BoletaVenta SET Total=@NuevoTotal WHERE NroBoleta=@NroBoleta;

        COMMIT TRAN;
        SELECT @NroBoleta AS NroBoleta, @TotalActual AS TotalAntes, @NuevoTotal AS TotalDespues;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRAN;
        DECLARE @Err NVARCHAR(4000)=ERROR_MESSAGE();
        RAISERROR('Error SP_AutorizarDescuento: %s',16,1,@Err);
    END CATCH
END;
GO

-- 5) CONSULTAS DE VALIDACIÓN RÁPIDA

---Ver tablas
SELECT * FROM Cliente
SELECT * FROM Vendedor
SELECT * FROM DetalleBoleta
SELECT * FROM DetalleFactura
SELECT * FROM BoletaVenta
SELECT * FROM Factura
SELECT * FROM Producto
GO

-- Clientes ordenados
SELECT * FROM Cliente ORDER BY ClienteNombre;
GO

-- Vendedor ordenados
SELECT * FROM Vendedor ORDER BY VendedorNombre;
GO

-- Productos por vencer (vista)
SELECT * FROM VW_ProductosProximosVencer ORDER BY FechaVencimiento;
GO

-- Boletas detalladas de un vendedor por nombre (vista)
SELECT * FROM VW_BoletasPorVendedor WHERE VendedorNombre = 'Lucero Chávez';
GO

-- Reporte: Boletas entre dos fechas
EXEC SP_ReporteBoletasRango '2025-01-01','2025-12-31';
GO

-- Resumen mensual (agosto 2025)
EXEC SP_ResumenMensualBoletas 2025, 8;
GO

-- Buscar producto
EXEC SP_BuscarProductoPorNombre 'Paracetamol';
GO

-- Regla: no vender vencido
EXEC SP_NoVenderProductoVencido 'P006';
GO

-- Tablas base (vistazo)
SELECT TOP 5 * FROM Producto ORDER BY CodigoProducto;
SELECT TOP 5 * FROM Vendedor ORDER BY VendedorID;
SELECT TOP 5 * FROM Cliente ORDER BY ClienteID;
SELECT TOP 5 * FROM BoletaVenta ORDER BY FechaEmision DESC;
SELECT TOP 5 * FROM Factura ORDER BY FechaEmision DESC;
GO
