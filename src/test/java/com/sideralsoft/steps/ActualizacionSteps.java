package com.sideralsoft.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class ActualizacionSteps {

    @Given("que existe proceso de actualizacion iniciado para los elementos:")
    public void queExisteProcesoDeActualizacionIniciadoParaLosElementos(DataTable table) {

    }

    @Then("en caso de estar en ejecucion, se detienen los procesos asociados al elemento a actualizar")
    public void EnCasoDeEstarEnEjecucionSeDetienenLosProcesosAsociadosAlElementoAActualizar() {
    }


    @And("se agregan, reemplazan, o eliminan los elementos antiguos con los nuevos segun sea necesario")
    public void seAgreganReemplazanOEliminanLosElementosAntiguosConLosNuevosSegunSeaNecesario() {
    }
}
