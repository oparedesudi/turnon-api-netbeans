package com.turnon.turnonapi.dao;

import com.turnon.turnonapi.config.ConexionBD;
import com.turnon.turnonapi.model.Turno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    public boolean crearTurno(Turno turno) {
        String sql = "INSERT INTO turno (codigo_turno, id_cliente, id_servicio, id_estado_turno, ventanilla) "
                   + "VALUES (?, ?, ?, ?, ?)";

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

        String sql = "SELECT id_turno, codigo_turno, fecha, id_cliente, id_servicio, id_estado_turno, ventanilla "
                   + "FROM turno ORDER BY id_turno DESC";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Turno turno = new Turno();
                turno.setIdTurno(rs.getInt("id_turno"));
                turno.setCodigoTurno(rs.getString("codigo_turno"));
                turno.setFecha(rs.getTimestamp("fecha"));
                turno.setIdCliente(rs.getInt("id_cliente"));
                turno.setIdServicio(rs.getInt("id_servicio"));
                turno.setIdEstadoTurno(rs.getInt("id_estado_turno"));
                turno.setVentanilla(rs.getString("ventanilla"));

                lista.add(turno);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
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
}