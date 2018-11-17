package Modelo;

public class Marcadores {
    private String idMarcador;
    private String idUsuario;
    private String nombre;
    private String descripcion;
    private String imagen;
    private String latitud;
    private String longitud;
    private String telefono;
    private String direccion;

    public Marcadores setIdUsuario(String idUsuario) { this.idUsuario = idUsuario;return this; }
    public Marcadores setIdMarcador(String idMarcador) { this.idMarcador = idMarcador;return this; }
    public Marcadores setNombre(String nombre) { this.nombre = nombre; return this;}
    public Marcadores setDescripcion(String descripcion) { this.descripcion = descripcion; return this;}
    public Marcadores setImagen(String imagen) { this.imagen = imagen; return this;}
    public Marcadores setLatitud(String posicion) { this.latitud = posicion; return this;}
    public Marcadores setLongitud(String longitud) { this.longitud = longitud; return this;}
    public Marcadores setTelefono(String telefono) { this.telefono = telefono;return this; }
    public Marcadores setDireccion(String direccion) { this.direccion = direccion;return this; }


    public String getIdUsuario() { return idUsuario; }
    public String getIdMarcador() { return idMarcador; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getImagen() { return imagen; }
    public String getLatitud() { return latitud; }
    public String getLongitud() { return longitud; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }


    public static class Favoritos {
        private String idFavoritos;
        private String idUsuarioSeguidor;
        private String fechaSeguir;


        public String getIdFavoritos() { return idFavoritos; }
        public String getIdUsuarioSeguidor() { return idUsuarioSeguidor; }
        public String getFechaSeguir() { return fechaSeguir; }

        public Favoritos setIdFavoritos(String idFavoritos) { this.idFavoritos = idFavoritos; return this;}
        public Favoritos setIdUsuarioSeguidor(String idUsuarioSeguidor) { this.idUsuarioSeguidor = idUsuarioSeguidor;return this; }
        public Favoritos setFechaSeguir(String fechaSeguir) { this.fechaSeguir = fechaSeguir;return this; }
    }
}