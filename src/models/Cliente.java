package models;

// RF-06 | RF-15 — Entidad Cliente
public class Cliente {

    private int    id;
    private String nombre;
    private String dni;
    private String telefono;

    public Cliente() {}

    public Cliente(int id, String nombre, String dni, String telefono) {
        setId(id);
        setNombre(nombre);
        setDni(dni);
        setTelefono(telefono);
    }

    // ── Getters y Setters con validación ─────────────────────────────────────

    public int getId() { return id; }

    public void setId(int id) {
        if (id < 0) throw new IllegalArgumentException("El id no puede ser negativo.");
        this.id = id;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        this.nombre = nombre.trim();
    }

    public String getDni() { return dni; }

    public void setDni(String dni) {
        if (dni == null || dni.trim().length() != 8)
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos.");
        this.dni = dni.trim();
    }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre=" + nombre + ", dni=" + dni +
               ", telefono=" + telefono + "}";
    }
}
