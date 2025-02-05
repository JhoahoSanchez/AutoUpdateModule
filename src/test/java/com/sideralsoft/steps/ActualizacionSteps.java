package com.sideralsoft.steps;

import com.sideralsoft.domain.Instalador;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.Proceso;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.exception.ActualizacionException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ActualizacionSteps {

    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionSteps.class);
    private final List<Elemento> elementosActualizables = new ArrayList<>();

    @Given("que existe proceso de actualizacion iniciado para los elementos:")
    public void queExisteProcesoDeActualizacionIniciadoParaLosElementos(DataTable table) {
        List<Map<String, String>> elementosMapList = table.asMaps(String.class, String.class);
        generarElementosPruebaCliente(elementosMapList);
        generarElementosPruebaAPI(elementosMapList);
    }

    @Then("en caso de estar en ejecucion, se detienen los procesos asociados al elemento a actualizar")
    public void enCasoDeEstarEnEjecucionSeDetienenLosProcesosAsociadosAlElementoAActualizar() {
        for (Elemento elemento : elementosActualizables) {
            Instalador instalador = new Instalador(elemento, "/ruta/temporal");

            try {
                instalador.detenerProcesos();
                validarProcesosDetenidos(elemento);
            } catch (Exception e) {
                LOG.error("Error al detener procesos de {}", elemento.getNombre(), e);
                fail("No se pudieron detener los procesos del elemento: " + elemento.getNombre());
            }
        }
    }

    @And("se agregan, reemplazan, o eliminan los elementos antiguos con los nuevos segun sea necesario")
    public void seAgreganReemplazanOEliminanLosElementosAntiguosConLosNuevosSegunSeaNecesario() {
        for (Elemento elemento : elementosActualizables) {
            Instalador instalador = new Instalador(elemento, "/ruta/temporal");

            try {
                instalador.actualizar();
                validarActualizacionExitosa(elemento);
            } catch (ActualizacionException e) {
                LOG.error("Error al actualizar el elemento {}", elemento.getNombre(), e);
                fail("Fallo la actualización del elemento: " + elemento.getNombre());
            }
        }
    }

    private void generarElementosPruebaCliente(List<Map<String, String>> elementosMapList) {
        List<Elemento> elementos = new ArrayList<>();
        for (Map<String, String> elementoData : elementosMapList) {
            Elemento elemento = new Elemento();
            elemento.setNombre(elementoData.get("Elemento"));
            elemento.setVersion(elementoData.get("VersionInstalada"));
            elemento.setTipo(TipoElemento.valueOf(elementoData.get("Tipo")));
            elementos.add(elemento);
        }
        ElementosSingleton.getInstance().actualizarArchivoElementos(elementos);
    }

    private void generarElementosPruebaAPI(List<Map<String, String>> elementosMapList) {
        for (Map<String, String> elementoData : elementosMapList) {
            Elemento elemento = new Elemento();
            elemento.setNombre(elementoData.get("Elemento"));
            elemento.setVersion(elementoData.get("VersionActualizable"));
            elemento.setTipo(TipoElemento.valueOf(elementoData.get("Tipo")));
            elementosActualizables.add(elemento);
        }
    }

    private void validarProcesosDetenidos(Elemento elemento) {
        if (elemento.getProcesos() == null || elemento.getProcesos().isEmpty()) {
            return;
        }
        for (Proceso proceso : elemento.getProcesos()) {
            assertFalse("El proceso " + proceso.getNombre() + " sigue en ejecución.", procesoEstaEnEjecucion(proceso));
        }
    }

    private void validarActualizacionExitosa(Elemento elemento) {
        String versionEsperada = obtenerVersionEsperada(elemento.getNombre());

        assertEquals("La versión instalada no coincide con la versión actualizada para " + elemento.getNombre(), elemento.getVersion(), versionEsperada);
    }

    private boolean procesoEstaEnEjecucion(Proceso proceso) {
        return false;
    }

    private String obtenerVersionEsperada(String nombreElemento) {
        return elementosActualizables.stream()
                .filter(e -> e.getNombre().equals(nombreElemento))
                .map(Elemento::getVersion)
                .findFirst()
                .orElse(null);
    }

}
