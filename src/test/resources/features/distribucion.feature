Feature: Distribucion de nuevas versiones

  Scenario: Cliente de escritorio solicita nueva version
    Given que existe peticion para descargar una nueva version de un elemento
    When se descarga el archivo comprimido
    Then el cliente inicia su proceso de actualizacion