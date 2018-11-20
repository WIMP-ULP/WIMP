package Modelo;

public class Oferta {

    private String idPublicidad;
    private String tituloOferta;
    private String idMarcador;
    private String precio;
    private String descripcionOferta;
    private String imgOferta;




    public Oferta setIdMarcador(String idMarcador) {
        this.idMarcador = idMarcador;
        return this;

    }

    public String getIdMarcador() {

        return idMarcador;
    }


    public Oferta setIdPublicidad(String idPublicidad) {
        this.idPublicidad = idPublicidad;        return this;
    }

    public Oferta setImgOferta(String imgOferta) {
        this.imgOferta = imgOferta;        return this;
    }

    public String getTituloOferta() {
        return tituloOferta;
    }

    public Oferta setTituloOferta(String tituloOferta) {
        this.tituloOferta = tituloOferta;        return this;
    }

    public String getPrecio() {
        return precio;
    }

    public Oferta setPrecio(String precio) {
        this.precio = precio;        return this;
    }

    public String getDescripcionOferta() {
        return descripcionOferta;
    }

    public Oferta setDescripcionOferta(String descripcionOferta) {
        this.descripcionOferta = descripcionOferta;
        return this;
    }



    public String getIdPublicidad() {
        return idPublicidad;
    }

    public String getImgOferta() {
        return imgOferta;
    }


}
