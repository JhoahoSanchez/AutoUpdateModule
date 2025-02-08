Feature: Instalacion de elementos

  Scenario: Instalación exitosa de un elemento
    Given un elemento de tipo "APLICACION" con nombre "MiAplicacion"
    And la versión "1.0.0"
    When se instala el elemento
    Then el elemento debe estar disponible en la ruta de instalación
