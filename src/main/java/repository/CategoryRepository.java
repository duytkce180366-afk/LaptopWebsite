/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dbcontext.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;

public class CategoryRepository extends DBContext {

    public List<Category> getAllCategories() {

        List<Category> list =
                new ArrayList<>();

        String sql =
        "SELECT * "
        + "FROM bs_Categories "
        + "ORDER BY category_name";

        try {

            PreparedStatement st =
                    connection.prepareStatement(sql);

            ResultSet rs =
                    st.executeQuery();

            while(rs.next()) {

                Category c =
                        new Category();

                c.setCategoryId(
                        rs.getInt("category_id"));

                c.setCategoryName(
                        rs.getString("category_name"));

                list.add(c);
            }

        } catch(Exception e) {

            e.printStackTrace();

        }

        return list;
    }
}
