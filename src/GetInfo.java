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

    @Path("/songs")
    @GET
    @Produces("application/json")
    public String getSong() throws SQLException {

        Statement stmt = null;
        String json = "{\"songs\":[";

        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306?useSSL=false";
           // String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
            Connection conn = DriverManager.getConnection(url, "root", "root");

            String query = "select id, artist, song, album, year from test.songs";

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("id");
                String artist = rs.getString("artist");
                String song = rs.getString("song");
                String album = rs.getString("album");
                int year = rs.getInt("year");
                json = json + "{ \"id\": " + id + ", \"artist\": \"" + artist + "\", \"song\": \"" + song + "\", \"album\": \"" + album + "\", \"year\": \"" + year + "\"},";
            }

            json = json.substring(0,json.length()-1) + "]}";

        } catch (SQLException e) {
            System.out.print(e);
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            if(json != "{\"songs\":["){
                return json;
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
