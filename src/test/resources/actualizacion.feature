Feature: Actualizacion de elementos

    Scenario: Actualizacion exitosa de un elemento
    Given que existe proceso de actualizacion iniciado
    Then se detienen los procesos asociados al elemento a actualizar
    And se agregan, reemplazan, o eliminan los elementos antiguos con los nuevos segun sea necesario