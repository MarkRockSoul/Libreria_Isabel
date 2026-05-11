package models;

// RF-01 | RF-12 — Entidad Usuario
public class Usuario {

    private int    id;
    private String usuario;
    private String contrasena;
    private String rol;   // "administrador" o "vendedor"

    public Usuario() {}

    public Usuario(int id, String usuario, String contrasena, String rol) {
        setId(id);
        setUsuario(usuario);
        setContrasena(contrasena);
        setRol(rol);
    }

    // ── Getters y Setters con validación ─────────────────────────────────────

    public int getId() { return id; }

    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("El id no puede ser negativo.");
        this.id = id;
    }

    public String getUsuario() { return usuario; }

    public void setUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty())
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        this.usuario = usuario.trim();
    }

    public String getContrasena() { return contrasena; }

    public void setContrasena(String contrasena) {
        if (contrasena == null || contrasena.isEmpty())
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        this.contrasena = contrasena;
    }

    public String getRol() { return rol; }

    public void setRol(String rol) {
        if (!rol.equals("administrador") && !rol.equals("vendedor"))
            throw new IllegalArgumentException("Rol inválido. Use 'administrador' o 'vendedor'.");
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", usuario=" + usuario + ", rol=" + rol + "}";
    }
}
