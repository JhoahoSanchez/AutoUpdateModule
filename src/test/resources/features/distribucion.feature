Feature: Distribucion de nuevas versiones

  Scenario: Cliente de escritorio solicita nueva version
    Given que existe una instalacion de un elemento:
      | Elemento          | Tipo       | Version |
      | interfaz-hormolab | INSTALADOR | 1.0.0   |
      | notepad-pp        | APLICACION | 8.5.0   |
    And que existe peticion para descargar una nueva version de un elemento:
      | Elemento          | Tipo       | Version |
      | interfaz-hormolab | INSTALADOR | 1.0.2   |
      | notepad-pp        | APLICACION | 8.5.7   |
    When se descarga el archivo y se descomprime en una carpeta temporal
    Then el cliente inicia su proceso de actualizacion