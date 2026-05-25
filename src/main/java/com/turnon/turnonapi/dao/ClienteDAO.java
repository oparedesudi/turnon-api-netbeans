package com.turnon.turnonapi.dao;

import com.turnon.turnonapi.config.ConexionBD;
import com.turnon.turnonapi.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public Cliente buscarPorCedula(String cedula) {
        String sql = "SELECT id_cliente, nombre, cedula, telefono, correo, fecha_registro "
                   + "FROM cliente WHERE cedula = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public int registrarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente (nombre, cedula, telefono, correo) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getCedula());
            ps.setString(3, cliente.getTelefono());
            ps.setString(4, cliente.getCorreo());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, telefono = ?, correo = ? WHERE cedula = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getCedula());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Cliente registrarOBuscarCliente(Cliente cliente) {
        Cliente existente = buscarPorCedula(cliente.getCedula());

        if (existente != null) {
            actualizarCliente(cliente);
            return buscarPorCedula(cliente.getCedula());
        }

        int idGenerado = registrarCliente(cliente);

        if (idGenerado > 0) {
            cliente.setIdCliente(idGenerado);
            return cliente;
        }

        return null;
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, cedula, telefono, correo, fecha_registro "
                   + "FROM cliente ORDER BY id_cliente DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientes;
    }

    private Cliente mapearCliente(ResultSet rs) throws Exception {
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