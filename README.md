# Farmacia Spring Boot

Sistema web académico para la gestión de una farmacia: clientes, productos, boletas de venta y facturas. Migración de un proyecto original en Servlets/DAO hacia Spring Boot MVC.

## 🛠️ Tecnologías

- Java 11
- Spring Boot 2.7 (Spring MVC, Spring JDBC)
- Thymeleaf
- SQL Server
- Maven

## 📋 Requisitos previos

- [JDK 11](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)
- SQL Server (Express, LocalDB o Developer Edition)

## 🚀 Instalación y ejecución local

**1. Clona el repositorio**
```bash
git clone https://github.com/tu-usuario/farmacia-spring-boot.git
cd farmacia-spring-boot
```

**2. Crea la base de datos**

Ejecuta el script `BoticaBD-2026.sql` incluido en el repositorio usando SQL Server Management Studio, Azure Data Studio, o por línea de comandos:
```bash
sqlcmd -S localhost -i BoticaBD-2026.sql
```
Esto crea la base `BoticaDB2026` con sus tablas, vistas, procedimientos y datos de prueba.

**3. Configura tu conexión**

Abre `src/main/resources/application.properties` y reemplaza `TU_USUARIO` y `TU_PASSWORD` con tus credenciales locales de SQL Server:
```properties
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```
> ⚠️ No subas tus credenciales reales a GitHub. Este archivo queda versionado con valores de ejemplo; edítalo solo en tu copia local.

**4. Ejecuta el proyecto**
```bash
mvn spring-boot:run
```
La aplicación estará disponible en `http://localhost:8081`.

## 📥 Importar en Spring Tools for Eclipse (opcional)

Opción recomendada:
```text
File > Import > Maven > Existing Maven Projects
```

También puedes usar:
```text
File > Import > General > Existing Projects into Workspace
```
gracias a los archivos `.project` y `.classpath` incluidos en el repositorio.

## 🧭 Rutas principales

- `/gestionproductos?accion=listar`
- `/gestionclientes?accion=listar`
- `/gestionboletas?accion=listar`

## 📸 Capturas de pantalla

_Agrega aquí 2-3 capturas del proyecto funcionando (listado de productos, registro de boleta, etc.)._

## 📄 Licencia

Proyecto desarrollado con fines académicos.
