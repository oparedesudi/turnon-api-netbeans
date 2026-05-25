package com.turnon.turnonapi.dao;

import com.turnon.turnonapi.config.ConexionBD;
import com.turnon.turnonapi.model.Cliente;
import com.turnon.turnonapi.model.Turno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    private static final int ESTADO_EN_ESPERA = 2;

    public Turno crearTurnoPersonalizado(Cliente cliente, int idServicio, String ventanilla) {
        Connection conn = null;

        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            Cliente clienteRegistrado = buscarClientePorCedula(conn, cliente.getCedula());

            if (clienteRegistrado == null) {
                int idCliente = insertarCliente(conn, cliente);
                cliente.setIdCliente(idCliente);
                clienteRegistrado = cliente;
            } else {
                actualizarCliente(conn, cliente);
                clienteRegistrado = buscarClientePorCedula(conn, cliente.getCedula());
            }

            String codigoTurno = generarCodigoTurno(conn);

            String sqlTurno = "INSERT INTO turno "
                    + "(codigo_turno, fecha, id_cliente, id_servicio, id_estado_turno, ventanilla) "
                    + "VALUES (?, NOW(), ?, ?, ?, ?)";

            int idTurnoGenerado;

            try (PreparedStatement ps = conn.prepareStatement(sqlTurno, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, codigoTurno);
                ps.setInt(2, clienteRegistrado.getIdCliente());
                ps.setInt(3, idServicio);
                ps.setInt(4, ESTADO_EN_ESPERA);
                ps.setString(5, ventanilla);

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idTurnoGenerado = rs.getInt(1);
                    } else {
                        conn.rollback();
                        return null;
                    }
                }
            }

            conn.commit();

            Turno turno = buscarTurnoPorId(idTurnoGenerado);
            return turno;

        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean crearTurno(Turno turno) {
        String sql = "INSERT INTO turno "
                + "(codigo_turno, fecha, id_cliente, id_servicio, id_estado_turno, ventanilla) "
                + "VALUES (?, NOW(), ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, turno.getCodigoTurno());
            ps.setInt(2, turno.getIdCliente());
            ps.setInt(3, turno.getIdServicio());
            ps.setInt(4, turno.getIdEstadoTurno());
            ps.setString(5, turno.getVentanilla());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Turno> listarTurnos() {
        List<Turno> lista = new ArrayList<>();

        String sql = "SELECT "
                + "t.id_turno, t.codigo_turno, t.fecha, t.id_cliente, t.id_servicio, "
                + "t.id_estado_turno, t.ventanilla, "
                + "c.nombre AS nombre_cliente, c.cedula AS cedula_cliente, "
                + "c.telefono AS telefono_cliente, c.correo AS correo_cliente, "
                + "s.nombre AS nombre_servicio, e.descripcion AS estado_turno "
                + "FROM turno t "
                + "LEFT JOIN cliente c ON t.id_cliente = c.id_cliente "
                + "LEFT JOIN servicio s ON t.id_servicio = s.id_servicio "
                + "LEFT JOIN estado_turno e ON t.id_estado_turno = e.id_estado_turno "
                + "ORDER BY t.id_turno DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearTurno(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Turno buscarTurnoPorId(int idTurno) {
        String sql = "SELECT "
                + "t.id_turno, t.codigo_turno, t.fecha, t.id_cliente, t.id_servicio, "
                + "t.id_estado_turno, t.ventanilla, "
                + "c.nombre AS nombre_cliente, c.cedula AS cedula_cliente, "
                + "c.telefono AS telefono_cliente, c.correo AS correo_cliente, "
                + "s.nombre AS nombre_servicio, e.descripcion AS estado_turno "
                + "FROM turno t "
                + "LEFT JOIN cliente c ON t.id_cliente = c.id_cliente "
                + "LEFT JOIN servicio s ON t.id_servicio = s.id_servicio "
                + "LEFT JOIN estado_turno e ON t.id_estado_turno = e.id_estado_turno "
                + "WHERE t.id_turno = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTurno);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearTurno(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean actualizarEstadoTurno(int idTurno, int idEstadoTurno) {
        String sql = "UPDATE turno SET id_estado_turno = ? WHERE id_turno = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idEstadoTurno);
            ps.setInt(2, idTurno);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Cliente buscarClientePorCedula(Connection conn, String cedula) throws Exception {
        String sql = "SELECT id_cliente, nombre, cedula, telefono, correo, fecha_registro "
                + "FROM cliente WHERE cedula = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setIdCliente(rs.getInt("id_cliente"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setCedula(rs.getString("cedula"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setCorreo(rs.getString("correo"));
                    cliente.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                    return cliente;
                }
            }
        }

        return null;
    }

    private int insertarCliente(Connection conn, Cliente cliente) throws Exception {
        String sql = "INSERT INTO cliente (nombre, cedula, telefono, correo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCedula());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getCorreo());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private void actualizarCliente(Connection conn, Cliente cliente) throws Exception {
        String sql = "UPDATE cliente SET nombre = ?, telefono = ?, correo = ? WHERE cedula = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getCedula());
            ps.executeUpdate();
        }
    }

    private String generarCodigoTurno(Connection conn) throws Exception {
        String sql = "SELECT COALESCE(MAX(id_turno), 0) + 1 AS siguiente FROM turno";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int siguiente = rs.getInt("siguiente");
                return "A" + String.format("%03d", siguiente);
            }
        }

        return "A001";
    }

    private Turno mapearTurno(ResultSet rs) throws Exception {
        Turno turno = new Turno();

        turno.setIdTurno(rs.getInt("id_turno"));
        turno.setCodigoTurno(rs.getString("codigo_turno"));
        turno.setFecha(rs.getTimestamp("fecha"));
        turno.setIdCliente(rs.getInt("id_cliente"));
        turno.setIdServicio(rs.getInt("id_servicio"));
        turno.setIdEstadoTurno(rs.getInt("id_estado_turno"));
        turno.setVentanilla(rs.getString("ventanilla"));

        turno.setNombreCliente(rs.getString("nombre_cliente"));
        turno.setCedulaCliente(rs.getString("cedula_cliente"));
        turno.setTelefonoCliente(rs.getString("telefono_cliente"));
        turno.setCorreoCliente(rs.getString("correo_cliente"));
        turno.setNombreServicio(rs.getString("nombre_servicio"));
        turno.setEstadoTurno(rs.getString("estado_turno"));

        return turno;
    }
}