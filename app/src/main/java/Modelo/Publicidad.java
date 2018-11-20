package Modelo;

import java.util.ArrayList;

/**
 * Created by dafunes on 12/10/2018.
 */

public class Publicidad {

ArrayList<Oferta> Listaoferta;

    private String fechaInicio;
    private String fechaFin;

    public String getFechaInicio() {
        return fechaInicio;
    }

    public Publicidad setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
        return this;
    }

    public Publicidad setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
        return this;
    }

    public String getFechaFin() {
        return fechaFin;
    }

}
