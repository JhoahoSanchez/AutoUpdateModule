package com.sideralsoft.steps;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.service.ConsultaService;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.MockApiClient;
import com.sideralsoft.utils.exception.ActualizacionException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SensarSteps {

    private final MockApiClient mockApiClient;
    private final List<String> respuestas;

    public SensarSteps() {
        mockApiClient = new MockApiClient();
        respuestas = new ArrayList<>();
    }

    @Given("que existe una nueva version de un elemento en el repositorio")
    public void queExisteUnaNuevaVersionDeUnElementoEnElRepositorio() {
    }

    @Given("el cliente tiene los siguientes elementos instalados:")
    public void elClienteTieneLosSiguientesElementos(DataTable dataTable) {
        List<Map<String, String>> elementosMapList = dataTable.asMaps(String.class, String.class);
        generarElementosPruebaCliente(elementosMapList);
    }

    @Given("el repositorio tiene los siguientes elementos:")
    public void elRepositorioTieneLosSiguienteElementos(DataTable dataTable) {
        List<Map<String, String>> elementosMapList = dataTable.asMaps(String.class, String.class);
        generarElementosPruebaAPI(elementosMapList);
    }

    @When("el cliente detecta una nueva version disponible en el repositorio")
    public void clienteDetectaNuevaVersion() {
        ConsultaService consultaService = new ConsultaService(mockApiClient);
        for (Elemento elemento : ElementosSingleton.getInstance().obtenerElementos()) {
            try {
                respuestas.add(consultaService.existeActualizacionDisponible(elemento));
            } catch (ActualizacionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Then("genera una tarea de actualizacion")
    public void generaTareaDeActualizacion() throws ActualizacionException {
        for (String respuesta : respuestas) {
            if (StringUtils.isBlank(respuesta)) {
                throw new ActualizacionException("Ha ocurrido un error al generar la tarea");
            }

            System.out.println("Tarea de actualizacion generada.");
        }
    }

    private void generarElementosPruebaCliente(List<Map<String, String>> elementosMapList) {
        List<Elemento> elementos = new ArrayList<>();
        for (Map<String, String> elementoData : elementosMapList) {
            String nombre = elementoData.get("Elemento");
            String version = elementoData.get("Version");
            elementos.add(new Elemento(nombre, version));
        }

        ElementosSingleton.getInstance().actualizarArchivoElementos(elementos);
    }

    private void generarElementosPruebaAPI(List<Map<String, String>> elementosMapList) {
        for (Map<String, String> elementoData : elementosMapList) {
            String nombre = elementoData.get("Elemento");
            String version = elementoData.get("Version");
            Elemento elementoCliente = ElementosSingleton.getInstance().obtenerElemento(nombre);

            mockApiClient
                    .addMockResponse(String.format("/buscar-actualizacion?nombre=%s&version=%s", nombre, elementoCliente.getVersion()),
                            "{\n" +
                                    "  \"mensaje\": \"Existe una nueva version\",\n" +
                                    "  \"actualizable\": true,\n" +
                                    "  \"version\": \" " + version + "\"\n" +
                                    "}"
                    );
        }
    }

}
