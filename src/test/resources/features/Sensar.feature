Feature: Sensado continuo de nuevas versiones

  Scenario: Nueva version disponible
    Given que existe una nueva version de un elemento en el repositorio
    And el cliente tiene los siguientes elementos instalados:
      | Elemento          | Version |
      | interfaz-hormolab | 1.0.0   |
      | interfaz-hpas     | 1.0.1   |
      | notepad-pp        | 8.5.7   |
      | pattern-matcher   | 2.1.2   |
    And el repositorio tiene los siguientes elementos:
      | Elemento          | Version |
      | interfaz-hormolab | 1.0.5   |
      | interfaz-hpas     | 1.0.4   |
      | notepad-pp        | 8.7.8   |
      | pattern-matcher   | 2.1.3   |
    When el cliente detecta una nueva version disponible en el repositorio
    Then genera una tarea de actualizacion