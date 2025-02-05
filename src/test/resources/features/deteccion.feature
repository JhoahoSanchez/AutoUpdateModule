Feature: Deteccion continua de nuevas versiones

  Scenario: Nueva version disponible
    Given que existe una nueva version de un elemento en el repositorio
    And el cliente tiene los siguientes elementos instalados:
      | Elemento          | Tipo       | Version |
      | interfaz-hormolab | APLICACION | 1.0.0   |
      | interfaz-hpas     | APLICACION | 1.0.1   |
      | notepad-pp        | APLICACION | 8.5.7   |
      | pattern-matcher   | INSTALADOR | 2.1.2   |
    And el repositorio tiene los siguientes elementos:
      | Elemento          | Tipo       | Version |
      | interfaz-hormolab | APLICACION | 1.0.5   |
      | interfaz-hpas     | APLICACION | 1.0.4   |
      | notepad-pp        | APLICACION | 8.7.8   |
      | pattern-matcher   | INSTALADOR | 2.1.3   |
    When el cliente detecta una nueva version disponible en el repositorio
    Then se genera una peticion para la descarga de la actualizacion