/**
 * Created by Quentin Van Ravels on 01-Mar-17.
 */

import javax.ws.rs.*;
import java.sql.*;

@Path("/play")
public class PlaySong {

    @Path("{c}")
    @POST
    public void getInput(@PathParam("c") int c) throws SQLException {
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

             String url = "jdbc:mysql://localhost:3306?useSSL=false";
            //String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
            Connection conn = DriverManager.getConnection(url, "root", "root");

            String query = "select artist, song from test.songs where id = " + c;

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);

                int id = rs.getInt("id");
                String artist = rs.getString("artist");
                String song = rs.getString("song");

            MqttJavaApplication app = new MqttJavaApplication();
            app.main(song, artist, null);

        } catch (SQLException e) {
            System.out.print(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }
}
