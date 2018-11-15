package Modelo;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Usuario {



    private String email;
    private String contraseña;

    public String getContraseña() {
        return contraseña;
    }

    public Usuario setContraseña(String contraseña) {
        this.contraseña = contraseña;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Usuario setEmail(String email) {
        this.email = email;
        return this;
    }

    public static class Premium{
        private String idPremium;
        private String idUsuario;
        private String fechaInicio;
        private String fechaFin;
        private String estado;


        public String getIdPremium() {
            return idPremium;
        }

        public Premium setIdPremium(String idPremium) {
            this.idPremium = idPremium;
            return this;
        }


        public String getIdUsuario() {
            return idUsuario;
        }

        public Premium setIdUsuario(String idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }


        public String getFechaInicio() {
            return fechaInicio;
        }

        public Premium setFechaInicio(String fechaInicio) {
            this.fechaInicio = fechaInicio;
            return this;
        }

        public String getFechaFin() {
            return fechaFin;
        }

        public Premium setFechaFin(String fechaFin) {
            this.fechaFin = fechaFin;
            return Usuario.Premium.this;
        }

        public String getEstado() {
            return estado;
        }

        public Premium setEstado(String estado) {
            this.estado = estado;
            return Usuario.Premium.this;
        }

        public boolean isExpired(){
            return new Date().after(new SimpleDateFormat().parse(fechaInicio,new ParsePosition(0)));
        }
    }




    public static class UsuarioPublico{
        private String idUsuario;
        private String nombre;
        private String apellido;
        private String imagen;
        public String getApellido() {
            return apellido;
        }

        public UsuarioPublico setApellido(String apellido) {
            this.apellido = apellido;
            return UsuarioPublico.this;
        }

        public String getNombre() {
            return nombre;
        }

        public UsuarioPublico setNombre(String nombre) {
            this.nombre = nombre;
            return UsuarioPublico.this;
        }

        public String getImagen() {
            return imagen;
        }

        public UsuarioPublico setImagen(String imagen) {
            this.imagen = imagen;
            return UsuarioPublico.this;
        }
        public String getIdUsuario() {
            return idUsuario;
        }

        public UsuarioPublico setIdUsuario(String idUsuario) {
            this.idUsuario = idUsuario;
            return  UsuarioPublico.this;
        }

    }

}
