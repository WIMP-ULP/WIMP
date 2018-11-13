package Modelo;

/**
 * Created by dafunes on 28/09/2018.
 */

public class Comentario {
    private String receptorID;

    public Comentario setReceptorID(String reseptorID) {
        this.receptorID = reseptorID;
        return this;
    }

    public String getReceptorID() {

        return receptorID;
    }

    private String emisorID;
    private String urlFoto;
    private String fechaHora;
    private String idComentario;
    private String idMarcador;
    private String cuerpo;




    public String getCuerpo() {
        return cuerpo;
    }

    public Comentario setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
        return this;
    }

    public Comentario setEmisorID(String emisorID) {
        this.emisorID = emisorID;
        return this;
    }

    public Comentario setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
        return this;
    }

    public Comentario setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
        return this;
    }

    public String getEmisorID() {
        return emisorID;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public String getFechaHora() {
        return fechaHora;
    }
    public Comentario setIdComentario(String idComentario) {
        this.idComentario = idComentario;
        return this;
    }

    public String getIdComentario() {return idComentario;}

    public String getIdMarcador() {return idMarcador;}

    public Comentario setIdMarcador(String idMarcador) {this.idMarcador = idMarcador;
        return this;}





}