# Gestor de actualizaciones - Core

El Core del Gestor de Actualizaciones permite la actualización de aplicaciones y certificados del almacén de Windows,
ya sea a través de una tarea programada que se ejecuta periódicamente o mediante la realización de una solicitud
externa.

---

## Tabla de Contenidos

1. [Descripción](#descripción)
2. [Funcionamiento](#funcionamiento)
    - [Instalación de aplicaciones portables](#instalación-de-aplicaciones-portables)
    - [Actualización de aplicaciones portables](#actualización-de-aplicaciones-portables)
    - [Instalación de aplicaciones mediante un "instalador silencioso"](#instalación-de-aplicaciones-mediante-un-instalador-silencioso)
    - [Actualización de aplicaciones mediante un "instalador silencioso"](#actualización-de-aplicaciones-mediante-un-instalador-silencioso)
3. [Requisitos del sistema](#requisitos-del-sistema)
4. [Instalación](#instalación)
5. [Compilación](#compilación)
6. [Empaquetado](#empaquetado)
7. [Licencia](#licencia)

---

## Descripción

Este proyecto fue desarrollado utilizando Java 21 y un servidor Spark para la creación de endpoints que facilitan la
recepción de peticiones. El núcleo del sistema permite configurar una tarea programada que se ejecuta cada 24 horas,
realizando una consulta a una API para obtener las actualizaciones disponibles. Las funcionalidades que ofrece el núcleo
son las siguientes:

- Instalación y actualización de aplicaciones portables.
- Instalación y actualización de aplicaciones mediante un "instalador silencioso".
- Instalación y actualización de certificados en el almacén de certificados de Windows.
- Instalación y actualización de dependencias de aplicaciones.
- Autoactualización del sistema.

El proyecto ofrece soporte para la ejecución de cada una de las funcionalidades mencionadas anteriormente a través de
peticiones HTTP. Este aspecto se explicará con más detalle en el apartado de [Funcionamiento](#funcionamiento).

---

## Funcionamiento

### Instalación de aplicaciones portables

El proyecto permite la instalación de aplicaciones portables tanto mediante peticiones HTTP como utilizando la interfaz
de línea de comandos (CLI) del proyecto.

**Proceso mediante petición HTTP:**

1. El usuario realiza una petición POST al endpoint `/install` con los parámetros *nombre* y *tipo* (APLICACION).

   ```http request
   POST http://localhost:5500/install?nombre=aplicacion-ejemplo&tipo=APLICACION
   ```
2. El núcleo consulta la API para verificar la disponibilidad de la aplicación.
3. Si la aplicación está disponible, el núcleo procede a descargar los archivos necesarios y los guarda en una carpeta
   temporal definida en `application.properties`.
4. A continuación, el núcleo copia los archivos descargados a una carpeta con el nombre de la aplicación, ubicada en la
   ruta base definida en `application.properties`.
5. El núcleo elimina los archivos temporales descargados.
6. Finalmente, el núcleo registra la aplicación en el archivo de configuraciones `appConfig.json`.

**Proceso mediante CLI**

1. El usuario ejecuta el comando `install`, proporcionando el *nombre* y *tipo* (APLICACION):

    ```bash
    appmanager install aplicacion-ejemplo APLICACION
    ```
2. El núcleo ejecuta los mismos pasos descritos en los puntos 2 a 6 del proceso mediante petición HTTP.

> **Nota:** Para las explicaciones de las demás funcionalidades, el proceso descrito en los puntos 2 a 6 se mantiene
> igual, a menos que se indique lo contrario.

### Actualización de aplicaciones portables

El proyecto permite la actualización de aplicaciones portables tanto mediante peticiones HTTP como utilizando la
interfaz
de línea de comandos (CLI) del proyecto.

**Proceso mediante petición HTTP:**

1. El usuario realiza una petición POST al endpoint `/update` con el parámetro *nombre*.

   ```http request
   POST http://localhost:5500/update?nombre=aplicacion-ejemplo
   ```

**Proceso mediante CLI**

1. El usuario ejecuta el comando `update`, proporcionando el *nombre*:

    ```bash
    appmanager update aplicacion-ejemplo
    ```

### Instalación de aplicaciones mediante un "instalador silencioso"

El proyecto permite la instalación de aplicaciones mediante un instalador (setup) tanto mediante peticiones HTTP como
utilizando la interfaz de línea de comandos (CLI) del proyecto.

> **Nota:** Para que el núcleo pueda realizar esta instalación, es necesario que el instalador haya sido creado con Inno
> Setup.

**Proceso mediante petición HTTP:**

1. El usuario realiza una petición POST al endpoint `/install` con el parámetro *nombre* y *tipo* (INSTALADOR).

   ```http request
   POST http://localhost:5500/install?nombre=aplicacion-ejemplo&tipo=INSTALADOR
   ```

**Proceso mediante CLI**

1. El usuario ejecuta el comando `install`, proporcionando el *nombre* y *tipo* (INSTALADOR):

    ```bash
    appmanager install aplicacion-ejemplo INSTALADOR
    ```

### Actualización de aplicaciones mediante un "instalador silencioso"

El proyecto permite la actualización de aplicaciones mediante un instalador (setup) tanto mediante peticiones HTTP como
utilizando la interfaz de línea de comandos (CLI) del proyecto.

> **Nota:** Para que el núcleo pueda realizar esta actualización, es necesario que el instalador haya sido creado con
> Inno Setup.

**Proceso mediante petición HTTP:**

1. El usuario realiza una petición POST al endpoint `/update` con el parámetro *nombre*.

   ```http request
   POST http://localhost:5500/update?nombre=aplicacion-ejemplo
   ```
2. El núcleo consulta la API para verificar la disponibilidad del instalador.
3. Si el instalador está disponible, el núcleo procede a descargar los archivos necesarios y los guarda en una carpeta
   temporal definida en `application.properties`.
4. Si la aplicación está en ejecución, el núcleo detiene sus procesos utilizando `taskkill`.
5. A continuación, el núcleo copia la instalación actual a una carpeta de respaldo con el nombre de la aplicación,
   ubicada en la ruta base definida en `application.properties`.
6. El núcleo ejecuta el desinstalador `unins000.exe`, ubicado en la raíz de la carpeta de instalación, utilizando la
   directiva `/VERYSILENT`.
7. El núcleo ejecuta el instalador descargado con la directiva `/VERYSILENT`.
8. El núcleo reinicia los procesos de la aplicación.
9. El núcleo elimina los archivos temporales descargados.
10. Finalmente, el núcleo actualiza la versión de la aplicación en el archivo de configuración `appConfig.json`.

**Proceso mediante CLI**

1. El usuario ejecuta el comando `update`, proporcionando el *nombre*:

    ```bash
    appmanager update aplicacion-ejemplo
    ```
2. El núcleo ejecuta los mismos pasos descritos en los puntos 2 a 10 del proceso mediante petición HTTP.

## Requisitos del sistema

Los requisitos mínimos para ejecutar este proyecto son:

- Procesador Intel Core i3
- 4 GB de RAM
- 1 GB de almacenamiento disponible
- Sistema operativo Windows 10 o superior

Además, para su ejecución, se requiere:

- Java 21

## Instalación en ambiente de desarrollo

Pasos para instalar las dependencias y preparar el entorno:

bash
Copiar
Editar

# Clonar el repositorio

git clone
cd proyecto

# Instalar dependencias

mvn compile

# Crear un ejecutable en Java

mvn clean package

## Licencia

Este proyecto fue desarrollado como trabajo de titulación por Jhoaho Gabriel Sánchez Cabrera, en el marco de la carrera
de Ingeniería en Software. Todos los derechos pertenecen a la Escuela Politécnica Nacional.

## Contacto

Para preguntas o comentarios, por favor contacta a: jhoaho.sanchez@epn.edu.ec
