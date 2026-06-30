/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.techstore.Repositories;

import dbcontext.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Brand;

public class BrandRepository extends DBContext {

    public List<Brand> getAllBrands() {

        List<Brand> list =
                new ArrayList<>();

        
        String sql =
        "SELECT * "
        + "FROM bs_Brands "
        + "ORDER BY brand_name";

        try {

            PreparedStatement st =
                    connection.prepareStatement(sql);

            ResultSet rs =
                    st.executeQuery();

            while(rs.next()) {

                Brand b =
                        new Brand();

                b.setBrandId(
                        rs.getInt("brand_id"));

                b.setBrandName(
                        rs.getString("brand_name"));

                list.add(b);
            }

        } catch(Exception e) {

            e.printStackTrace();

        }

        return list;
    }
    
}