package com.sideralsoft.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.Proceso;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.FileManager;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.JsonUtils;
import com.sideralsoft.utils.exception.ActualizacionException;
import com.sideralsoft.utils.http.InstruccionResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ActualizacionSteps {

    private static final Logger LOG = LoggerFactory.getLogger(ActualizacionSteps.class);
    private final List<Elemento> elementosActualizables = new ArrayList<>();
    private final Map<String, List<InstruccionResponse>> instruccionesActualizacion = new HashMap<>();

    @Before(order = 1)
    public void generarArchivosPrueba() throws IOException {
        //Genera instalacion local para prueba
        FileManager.copyFolder(Paths.get("src/test/resources/files/instalaciones"), Paths.get("src/test/resources/mockInstalacion"));
        //Genera archivos de actualizacion para prueba
        FileManager.copyFolder(Paths.get("src/test/resources/files/descomprimidos"), Paths.get("src/test/resources/mockInstalacion/temp"));
    }

    @Given("que existe proceso de actualizacion iniciado para los elementos:")
    public void queExisteProcesoDeActualizacionIniciadoParaLosElementos(DataTable table) {
        List<Map<String, String>> elementosMapList = table.asMaps(String.class, String.class);
        generarElementosPruebaCliente(elementosMapList);
        generarElementosPruebaAPI(elementosMapList);
    }

    @And("se cuenta con las siguientes instrucciones de actualizacion:")
    public void seCuentaConLasSiguientesInstruccionesDeActualizacion(DataTable table) {
        List<Map<String, String>> instruccionesMapList = table.asMaps(String.class, String.class);
        generarInstruccionesActualizacion(instruccionesMapList);
    }

    @Then("en caso de estar en ejecucion, se detienen los procesos asociados al elemento a actualizar")
    public void enCasoDeEstarEnEjecucionSeDetienenLosProcesosAsociadosAlElementoAActualizar() {
        for (Elemento elemento : elementosActualizables) {
            Aplicacion aplicacion = new Aplicacion(elemento);

            try {
                aplicacion.detenerProcesos();
            } catch (Exception e) {
                LOG.error("Error al detener procesos de {}", elemento.getNombre(), e);
                fail("No se pudieron detener los procesos del elemento: " + elemento.getNombre());
            }
        }
    }

    @And("se agregan, reemplazan, o eliminan los elementos antiguos con los nuevos segun sea necesario")
    public void seAgreganReemplazanOEliminanLosElementosAntiguosConLosNuevosSegunSeaNecesario() {
        for (Elemento elemento : elementosActualizables) {
            Aplicacion aplicacion = new Aplicacion(
                    elemento,
                    Paths.get(
                            ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"),
                            elemento.getNombre()
                    ).toString(),
                    instruccionesActualizacion.get(elemento.getNombre())
            );

            try {
                aplicacion.actualizar();
                validarActualizacionExitosa(elemento);
            } catch (ActualizacionException e) {
                LOG.error("Error al actualizar el elemento {}", elemento.getNombre(), e);
                fail("Fallo la actualización del elemento: " + elemento.getNombre());
            }
        }
    }

    @After(order = 2)
    public void borrarArchivosDePrueba() throws IOException {
        FileManager.deleteFolder(Paths.get("src/test/resources/mockInstalacion"));
    }

    private void generarElementosPruebaCliente(List<Map<String, String>> elementosMapList) {
        List<Elemento> elementos = new ArrayList<>();
        for (Map<String, String> elementoData : elementosMapList) {
            Elemento elemento = new Elemento();
            elemento.setNombre(elementoData.get("Elemento"));
            elemento.setVersion(elementoData.get("VersionInstalada"));
            elemento.setTipo(TipoElemento.valueOf(elementoData.get("Tipo")));
            elemento.setRuta(Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), elemento.getNombre()).toString());
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
            elemento.setRuta(Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), elemento.getNombre()).toString());
            elementosActualizables.add(elemento);
        }
    }

    private void generarInstruccionesActualizacion(List<Map<String, String>> instruccionesMapList) {
        for (Map<String, String> instruccionData : instruccionesMapList) {
            try {
                instruccionesActualizacion.put(
                        instruccionData.get("Elemento"),
                        JsonUtils.fromJsonToList(instruccionData.get("Instrucciones"), InstruccionResponse.class)
                );
            } catch (JsonProcessingException e) {
                fail("Ha ocurrido un error al crear las instrucciones de prueba.");
            }
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
        assertTrue(Files.exists(Paths.get(elemento.getRuta(), instruccionesActualizacion.get(elemento.getNombre()).getFirst().getRuta())));
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
