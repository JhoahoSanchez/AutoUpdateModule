package com.sideralsoft.steps;

import com.sideralsoft.config.ApplicationProperties;
import com.sideralsoft.domain.Aplicacion;
import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.utils.FileManager;
import com.sideralsoft.utils.exception.InstalacionException;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class InstalacionSteps {

    private final List<Elemento> elementosInstalables = new ArrayList<>();

    @Before(order = 1)
    public void generarArchivosPrueba() throws IOException {
        FileManager.copyFolder(Paths.get("src/test/resources/files/descomprimidosInstalacion"), Paths.get("src/test/resources/mockInstalacion/temp"));
    }

    @Given("que existen los siguientes elementos en la API:")
    public void queExistenLosSiguientesElementosEnLaAPI(DataTable dataTable) {
        List<Map<String, String>> elementosMapList = dataTable.asMaps(String.class, String.class);
        generarElementosPruebaAPI(elementosMapList);
    }

    @When("se instala el elemento")
    public void seInstalaElElemento() {
        for (Elemento elemento : elementosInstalables) {
            Aplicacion aplicacion = new Aplicacion(elemento, Paths.get(
                    ApplicationProperties.getProperty("app.config.storage.rutaAlmacenamientoTemporal"),
                    elemento.getNombre()
            ).toString());

            try {
                aplicacion.instalar();
            } catch (InstalacionException e) {
                fail("Fallo la instalacion del elemento: " + elemento.getNombre());
            }
        }
    }

    @Then("el elemento debe estar disponible en la ruta de instalacion")
    public void elElementoDebeEstarDisponibleEnLaRutaDeInstalacion() {
        for (Elemento elemento : elementosInstalables) {
            System.out.println(elemento);
            assertTrue(Files.exists(Paths.get(elemento.getRuta())));
        }
    }

    @After(order = 2)
    public void borrarArchivosDePrueba() throws IOException {
        FileManager.deleteFolder(Paths.get("src/test/resources/mockInstalacion"));
    }

    private void generarElementosPruebaAPI(List<Map<String, String>> elementosMapList) {
        for (Map<String, String> elementoData : elementosMapList) {
            Elemento elemento = new Elemento();
            elemento.setNombre(elementoData.get("Elemento"));
            elemento.setVersion(elementoData.get("VersionActualizable"));
            elemento.setTipo(TipoElemento.valueOf(elementoData.get("Tipo")));
            elemento.setRuta(Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), elemento.getNombre()).toString());
            elementosInstalables.add(elemento);
        }
    }
}
