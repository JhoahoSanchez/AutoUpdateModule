Feature: Actualizacion de elementos

  Scenario: Actualizacion exitosa de un elemento
    Given que existe proceso de actualizacion iniciado para los elementos:
      | Elemento          | Tipo       | VersionInstalada | VersionActualizable |
      | interfaz-hormolab | APLICACION | 1.0.0            | 1.0.2               |
      | notepad-pp        | APLICACION | 8.5.0            | 8.5.7               |
    And se cuenta con las siguientes instrucciones de actualizacion:
      | Elemento          | Instrucciones                                                               |
      | interfaz-hormolab | [{"elemento": "config.conf","ruta": "bin/config.conf","accion": "AGREGAR"}] |
      | notepad-pp        | [{"elemento": "config.conf","ruta": "bin/config.conf","accion": "AGREGAR"}] |
    Then en caso de estar en ejecucion, se detienen los procesos asociados al elemento a actualizar
    And se agregan, reemplazan, o eliminan los elementos antiguos con los nuevos segun sea necesario