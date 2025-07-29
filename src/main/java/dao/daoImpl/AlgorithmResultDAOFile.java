package dao.daoImpl;

import dao.AlgorithmResultDAO;
import models.AlgorithmResult;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmResultDAOFile implements AlgorithmResultDAO {

    private final File archivo;

    public AlgorithmResultDAOFile() {
        this("results.csv");
    }

    public AlgorithmResultDAOFile(String rutaArchivo) {
        this.archivo = new File(rutaArchivo);
    }

    @Override
    public void guardarResultado(AlgorithmResult nuevo) {
        List<AlgorithmResult> existentes = obtenerResultados();
        boolean reemplazado = false;

        for (int i = 0; i < existentes.size(); i++) {
            if (existentes.get(i).getAlgorithmName().equalsIgnoreCase(nuevo.getAlgorithmName())) {
                existentes.set(i, nuevo);
                reemplazado = true;
                break;
            }
        }

        if (!reemplazado) {
            existentes.add(nuevo);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo, false))) {
            for (AlgorithmResult r : existentes) {
                writer.println(r.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar resultados: " + e.getMessage());
        }
    }

    @Override
    public List<AlgorithmResult> obtenerResultados() {
        List<AlgorithmResult> resultados = new ArrayList<>();
        if (!archivo.exists()) return resultados;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    String nombre = partes[0];
                    int longitud = Integer.parseInt(partes[1]);
                    long tiempo = Long.parseLong(partes[2]);
                    LocalDateTime timestamp = LocalDateTime.parse(partes[3]);
                    resultados.add(new AlgorithmResult(nombre, longitud, tiempo));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al leer resultados: " + e.getMessage());
        }

        return resultados;
    }

    @Override
    public void limpiarResultados() {
        if (archivo.exists() && archivo.delete()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                System.err.println("Error al regenerar archivo: " + e.getMessage());
            }
        }
    }
}