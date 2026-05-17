package models;

public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String password;
    private String rol; // ADMINISTRADOR o VENDEDOR
    private String nombres;
    private String apellidos;

    // Constructores
    public Usuario() {
    }

    public Usuario(int idUsuario, String nombreUsuario, String password, String rol, 
                   String nombres, String apellidos) {
        setIdUsuario(idUsuario);
        setNombreUsuario(nombreUsuario);
        setPassword(password);
        setRol(rol);
        setNombres(nombres);
        setApellidos(apellidos);
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        if (!rol.equals("ADMINISTRADOR") && !rol.equals("VENDEDOR")) {
            throw new IllegalArgumentException("Rol inválido. Debe ser ADMINISTRADOR o VENDEDOR");
        }
        this.rol = rol;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public boolean validarPassword(String password) {
        return this.password.equals(password);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombreUsuario='" + nombreUsuario + '\'' +
                ", rol='" + rol + '\'' +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                '}';
    }
}