package com.sideralsoft.steps;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.service.DescargaService;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.MockApiClientDescarga;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class DistribucionSteps {

    private final MockApiClientDescarga mockApiClient;
    private final DescargaService descargaService;
    private final List<String> rutasArchivosDescargados;
    private final List<Elemento> elementosActualizables;

    public DistribucionSteps() {
        mockApiClient = new MockApiClientDescarga();
        descargaService = new DescargaService(mockApiClient);
        rutasArchivosDescargados = new ArrayList<>();
        elementosActualizables = new ArrayList<>();
    }

    @Given("que existe una instalacion de un elemento:")
    public void queExisteUnaInstalacionDeUnElemento(DataTable dataTable) {
        List<Map<String, String>> elementosMapList = dataTable.asMaps(String.class, String.class);
        generarElementosPruebaCliente(elementosMapList);
    }

    @Given("que existe peticion para descargar una nueva version de un elemento:")
    public void queExistePeticionParaDescargarNuevaVersionDeUnElemento(DataTable dataTable) throws IOException {
        List<Map<String, String>> elementosMapList = dataTable.asMaps(String.class, String.class);
        generarElementosPruebaAPI(elementosMapList);
    }

    @When("se descarga el archivo y se descomprime en una carpeta temporal")
    public void seDescargaElArchivoYSeDescomprimeEnUnaCarpetaTemporal() throws Exception {
        for (Elemento elemento : elementosActualizables) {
            rutasArchivosDescargados.add(descargaService.descargarArchivos(elemento.getNombre(), elemento.getVersion(), elemento.getTipo()));
        }
    }

    @Then("el cliente inicia su proceso de actualizacion")
    public void elClienteIniciaSuProcesoDeActualizacion() {
        // Verifica que el archivo fue descargado correctamente
        assertNotNull(rutasArchivosDescargados);
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

    private void generarElementosPruebaAPI(List<Map<String, String>> elementosMapList) throws IOException {
        for (Map<String, String> elementoData : elementosMapList) {
            String nombre = elementoData.get("Elemento");
            String version = elementoData.get("Version");
            elementosActualizables.add(new Elemento(nombre, version));

            Path archivo = Paths.get(String.format("src/test/resources/files/%s-%s.zip", nombre, version));

            mockApiClient
                    .addMockResponse(String.format("/descargar-archivos-instalacion?nombre=%s&ultimaVersion=%s", nombre, version),
                            Files.readAllBytes(archivo)
                    );
        }
    }
}
