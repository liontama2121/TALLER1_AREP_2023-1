package escuela.ing.edu.app;

import java.net.*;
import java.io.*;
import java.util.HashMap;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        HttpConnectionExample conexionAPI = new HttpConnectionExample();
        ServerSocket serverSocket = null;
        HashMap <String , String> cache = new HashMap<>();
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Socket clientSocket = null;
        Boolean corriendo = true;
        while (corriendo) {


            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;
            String outputLine = "no cambia";
            Boolean primeralinea = true;
            String ruta = "/HOME";
            while ((inputLine = in.readLine()) != null) {
                if (primeralinea) {
                    ruta = inputLine.split(" ")[1];
                    primeralinea = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            String response ="";
            if (ruta.split("/").length > 1) {
                if (ruta.split("/")[1].equals("get")) {
                    if(cache.containsKey(ruta.split("=")[1])){
                        System.out.println("entro cache");
                        response = cache.get(ruta.split("=")[1]);
                    }
                    else{
                        response = conexionAPI.UsarApi(ruta.split("/")[2]);
                        cache.put(ruta.split("=")[1],response);
                    }

                }
            } else {
                response = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "\r\n" +
                        "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<title>Form Example</title>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>Form with GET</h1>\n" +
                        "<form action=\"/get\">\n" +
                        "<label for=\"name\">Name:</label><br>\n" +
                        "<input type=\"text\" id=\"name\" value=\"John\"><br><br>\n" +
                        "<input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                        "</form>s\n" +
                        "<div id=\"getrespmsg\"></div>\n" +
                        "s\n" +
                        "<script>\n" +
                        "function loadGetMsg(){\n" +
                        "let name = document.getElementById(\"name\");\n" +
                        "let url = \"get/?t=\" + name.value;\n" +
                        "fetch (url, {method: 'GET'})\n" +
                        ".then(x => x.text())\n" +
                        ".then(y => document.getElementById(\"getrespmsg\").innerHTML = y);\n" +
                        "}\n" +
                        "</script>\n" +
                        "</body>\n" +
                        "</html>";
            }
            outputLine = response;
            out.println(outputLine);


            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
}