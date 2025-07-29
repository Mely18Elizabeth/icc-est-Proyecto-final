package dao;

import models.AlgorithmResult;

import java.util.List;

public interface AlgorithmResultDAO {
    void guardarResultado(AlgorithmResult resultado);
    List<AlgorithmResult> obtenerResultados();
    void limpiarResultados();
}