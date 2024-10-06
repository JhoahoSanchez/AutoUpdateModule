@echo off

REM Ruta del nuevo certificado
set CERT_PATH=D:\Universidad\Tesis\AutoUpdateV1.0.0\AutoUpdateModule_2.0\AutoUpdateModule\src\main\resources\Certificados\server700.crt

REM Alias del certificado que deseas actualizar
set ALIAS=Sideralsoft

REM Eliminar el certificado existente (sin confirmación)
certutil -delstore Root "%ALIAS%"

REM Importar el nuevo certificado
certutil -addstore Root "%CERT_PATH%"

echo Certificado actualizado exitosamente.