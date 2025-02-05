Feature: Actualizacion de elementos

  Scenario: Actualizacion exitosa de un elemento
    Given que existe proceso de actualizacion iniciado para los elementos:
      | Elemento           | Tipo       | VersionInstalada | VersionActualizable |
      | orion-print-server | INSTALADOR | 1.0.0            | 1.0.2               |
      | notepad-pp         | INSTALADOR | 8.5.0            | 8.5.7               |
    Then en caso de estar en ejecucion, se detienen los procesos asociados al elemento a actualizar
    And se agregan, reemplazan, o eliminan los elementos antiguos con los nuevos segun sea necesario