Feature: Sensado continuo de nuevas versiones

    Scenario: Nueva version disponible
    Given que existe una nueva version de un elemento
    When el cliente detecta una nueva version disponible en el repositorio
    Then genera una tarea de actualizacion

    Scenario: No hay nueva version disponible
    Given que el elemento se encuentra en su version mas actual
    When el cliente no detecta una nueva version
    Then genera una nueva tarea de sensado a ser ejecutada luego de un tiempo determinado