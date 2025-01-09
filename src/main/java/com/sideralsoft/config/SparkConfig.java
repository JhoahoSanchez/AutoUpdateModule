package com.sideralsoft.config;

import com.sideralsoft.domain.model.Elemento;
import com.sideralsoft.domain.model.Proceso;
import com.sideralsoft.domain.model.TipoElemento;
import com.sideralsoft.service.ActualizacionService;
import com.sideralsoft.service.ConsultaService;
import com.sideralsoft.service.InstalacionService;
import com.sideralsoft.utils.ElementosSingleton;
import com.sideralsoft.utils.JsonUtils;
import com.sideralsoft.utils.http.InstalacionResponse;
import com.sideralsoft.utils.http.InstruccionResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class SparkConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SparkConfig.class);
    private static SparkConfig instance;

    private final ConsultaService consultaService;
    private final ActualizacionService actualizacionService;
    private final InstalacionService instalacionService;

    private SparkConfig() {
        consultaService = new ConsultaService();
        actualizacionService = new ActualizacionService();
        instalacionService = new InstalacionService();

        configurarSpark();
        configurarRutas();
    }

    public static SparkConfig getInstance() {
        if (instance == null) {
            instance = new SparkConfig();
        }
        return instance;
    }

    private void configurarSpark() {
        int port = Integer.parseInt(ApplicationProperties.getProperty("spark.defaultPort"));
        Spark.port(port);
        int maxThreads = Integer.parseInt(ApplicationProperties.getProperty("spark.maxThreads"));
        Spark.threadPool(maxThreads);
    }

    private void configurarRutas() {
        get("version", (req, res) -> {
            res.type("text/plain");

            Elemento elemento = ElementosSingleton.getInstance().obtenerElemento(req.queryParams("nombre"));
            if (elemento != null) {
                res.status(200);
                return elemento.getVersion();
            }

            res.status(404);
            return "No se encontro el elemento";
        });

        post("install", (req, res) -> {
            res.type("text/plain");

            try {
                String nombre = req.queryParams("nombre");
                TipoElemento tipo = TipoElemento.valueOf(req.queryParams("tipo"));

                if (ElementosSingleton.getInstance().obtenerElemento(nombre) != null) {
                    res.status(500);
                    return "Ya existe una instalacion de " + nombre;
                }

                InstalacionResponse instalacionDisponible = consultaService.existeInstalacionDisponible(nombre);

                LOG.debug("Instalacion disponible: " + JsonUtils.toJson(instalacionDisponible));

                if (instalacionDisponible == null) {
                    res.status(500);
                    return "Ha ocurrido un error durante la consulta de " + nombre + ", revise el log";
                }

                Elemento elemento = new Elemento();
                elemento.setNombre(nombre);
                elemento.setTipo(tipo);

                if (tipo.equals(TipoElemento.APLICACION)) {
                    elemento.setRuta(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion") + "\\" + nombre);
                    Path rutaInstalacion = Paths.get(ApplicationProperties.getProperty("app.config.storage.rutaInstalacion"), nombre);

                    if (instalacionDisponible.getProcesos() != null) {
                        List<Proceso> procesos = new ArrayList<>();

                        for (Proceso proceso : instalacionDisponible.getProcesos()) {
                            procesos.add(
                                    new Proceso(proceso.getNombre(),
                                            Paths.get(elemento.getRuta(), proceso.getRuta()).toString()
                                    )
                            );
                        }

                        elemento.setProcesos(procesos);
                    }

                    if (!Files.exists(rutaInstalacion)) {
                        if (!rutaInstalacion.toFile().mkdirs()) {
                            res.status(500);
                            return "Error al crear los directorios de instalacion";
                        }
                    }
                    elemento.setRuta(rutaInstalacion.toFile().getAbsolutePath());
                }

                if (!instalacionService.instalarElemento(elemento, instalacionDisponible.getVersion())) {
                    res.status(500);
                    return "Ha ocurrido un error durante la instalacion.";
                }

                elemento.setVersion(instalacionDisponible.getVersion());

                List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();
                elementos.add(elemento);

                ElementosSingleton.getInstance().actualizarArchivoElementos(elementos);
                ElementosSingleton.getInstance().obtenerElementos();

                return elemento.getNombre() + " ha sido instalado exitosamente en la version " + elemento.getVersion();
            } catch (Exception e) {
                LOG.error("Error general al intentar instalar los elementos: ", e);
                res.status(500);
                return "Error al intentar instalar los elementos";
            }
        });

        post("update", (req, res) -> {
            res.type("text/plain");

            Elemento elemento = ElementosSingleton.getInstance().obtenerElemento(req.queryParams("nombre"));
            if (elemento == null) {
                res.status(404);
                return "No se encontro el elemento";
            }

            try {
                String version = consultaService.existeActualizacionDisponible(elemento);

                if (StringUtils.isBlank(version)) {
                    res.status(204);
                    return "No se ha encontrado una actualizacion disponible para " + elemento.getNombre();
                }

                List<InstruccionResponse> instrucciones = consultaService.obtenerInstrucciones(elemento, version);

                if (instrucciones == null || instrucciones.isEmpty()) {
                    LOG.debug("No se ha encontrado instrucciones de actualizacion para " + elemento.getNombre());
                    res.status(204);
                    return "No se ha encontrado instrucciones de actualizacion para " + elemento.getNombre();
                }

                if (!actualizacionService.actualizarElemento(elemento, instrucciones, version)) {
                    res.status(500);
                    LOG.debug("No se ha podido actualizar el elemento");
                    return "No se ha podido actualizar el elemento";
                }

                elemento.setVersion(version);
                List<Elemento> elementos = ElementosSingleton.getInstance().obtenerElementos();
                List<Elemento> elementosActualizados = elementos.stream()
                        .map(e -> e.getNombre().equals(elemento.getNombre()) ? elemento : e)
                        .toList();

                ElementosSingleton.getInstance().actualizarArchivoElementos(elementosActualizados);
                ElementosSingleton.getInstance().obtenerElementos();

                return elemento.getNombre() + " ha sido actualizado exitosamente a la version " + elemento.getVersion();
            } catch (Exception e) {
                LOG.error("Error general al intentar actualizar los elementos: ", e);
                res.status(500);
                return "Error al intentar actualizar los elementos";
            }
        });
    }
}
