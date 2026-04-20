package com.turnon.turnonapi.dao;

import com.turnon.turnonapi.config.ConexionBD;
import com.turnon.turnonapi.model.EstadoTurno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EstadoTurnoDAO {

    public List<EstadoTurno> listarEstadosTurno() {
        List<EstadoTurno> lista = new ArrayList<>();

        String sql = "SELECT id_estado_turno, descripcion FROM estado_turno ORDER BY id_estado_turno";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EstadoTurno estado = new EstadoTurno();
                estado.setIdEstadoTurno(rs.getInt("id_estado_turno"));
                estado.setDescripcion(rs.getString("descripcion"));
                lista.add(estado);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}