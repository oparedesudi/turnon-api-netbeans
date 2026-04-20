package com.turnon.turnonapi.dao;

import com.turnon.turnonapi.config.ConexionBD;
import com.turnon.turnonapi.model.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    public List<Servicio> listarServicios() {
        List<Servicio> lista = new ArrayList<>();

        String sql = "SELECT id_servicio, nombre, id_tipo_servicio FROM servicio";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Servicio servicio = new Servicio();
                servicio.setIdServicio(rs.getInt("id_servicio"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setIdTipoServicio(rs.getInt("id_tipo_servicio"));
                lista.add(servicio);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}