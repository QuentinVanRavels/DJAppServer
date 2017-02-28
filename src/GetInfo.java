/**
 * Created by Quentin Van Ravels on 28-Feb-17.
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.sql.*;



@Path("/info")
public class GetInfo {

    @GET
    @Produces("application/json")
    public String getSong() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/test?useSSL=false";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn = DriverManager.getConnection(url, "root", "root");

        Statement stmt = null;
        String query = "select title, artist from test.songs";

        try {
            stmt = conn.createStatement();
            String json = null;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                System.out.println("title: " + title + ", artist: " + artist);
                json = json + "title: " + title + ", artist" + artist;
            }

            return json;

        } catch (SQLException e) {
            System.out.print(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }


            return "Error";
            // String json = "{ \"Title\":\"Knights of Cydonia\", \"Artist\":\"Muse\"}";
            // return json;
        }
    }

    @Path("{c}")
    @GET
    @Produces("application/json")
    public String getInput(@PathParam("c") String c){
        String input = c;
        String json = "{ \"Input\":\"" + c + "\"}";

        System.out.print(json);

        return json;
    }
}
