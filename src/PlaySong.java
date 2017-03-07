/**
 * Created by Quentin Van Ravels on 01-Mar-17.
 */

import org.springframework.web.bind.annotation.CrossOrigin;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.*;

@CrossOrigin
@Path("/play")
public class PlaySong {

    @Path("{c}")
    @GET
    public Response getInput(@PathParam("c") int c) throws SQLException,IOException {
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            // String url = "jdbc:mysql://localhost:3306?useSSL=false";
            String url = "jdbc:mysql://143.129.39.117:3306?useSSL=false";
            Connection conn = DriverManager.getConnection(url, "Dries", "password");

            String query = "select artist, song from sqldb.Track where id = " + c;

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            Socket socket = new Socket("localhost",6789);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String songData = String.valueOf(c)+"#%"+rs.getString("song")+"#%"+rs.getString("artist")+"#%album"+"#%"+String.valueOf(rs.getInt("year"));
            socket.close();
            dataOutputStream.writeBytes(songData + '\n');
            //app.sendMessage(c,rs.getString("song"),rs.getString("artist"),rs.getString("album"),rs.getInt("year"));


        } catch (SQLException e) {
            System.out.print(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        String result = "{\"status\" : \"ok\"}";

        return Response.status(200).entity(result).header("Access-Control-Allow-Origin", "*").build();

    }
}
