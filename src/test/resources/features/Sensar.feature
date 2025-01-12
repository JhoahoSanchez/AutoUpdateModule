Feature: Sensado continuo de nuevas versiones

  Scenario: Nueva version disponible
    Given que existe una nueva version de un elemento en el repositorio
    And el repositorio tiene los siguientes elementos:
      | Elemento          | Version |
      | interfaz-hormolab | 1.0.5   |
      | interfaz-hpas     | 1.0.4   |
      | notepad-pp        | 8.7.8   |
      | pattern-matcher   | 2.1.3   |
    And el cliente tiene los siguientes elementos instalados:
      | Elemento          | Version |
      | interfaz-hormolab | 1.0.0   |
      | interfaz-hpas     | 1.0.1   |
      | notepad-pp        | 8.5.7   |
      | pattern-matcher   | 2.1.2   |
    When el cliente detecta una nueva version disponible en el repositorio
    Then genera una tarea de actualizacion

  Scenario: No hay nueva version disponible
    Given que el elemento se encuentra en su version mas actual
    When el cliente no detecta una nueva version
    Then genera una nueva tarea de sensado a ser ejecutada luego de un tiempo determinado