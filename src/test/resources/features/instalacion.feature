Feature: Instalacion de elementos

  Scenario: Instalación exitosa de un elemento
    Given que existen los siguientes elementos en la API:
      | Elemento        | Tipo       | Version |
      | interfaz-hpas   | APLICACION | 1.0.0   |
      | pattern-matcher | APLICACION | 1.0.2   |
    When se instala el elemento
    Then el elemento debe estar disponible en la ruta de instalacion
