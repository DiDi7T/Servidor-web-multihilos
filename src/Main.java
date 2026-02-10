import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.StringTokenizer;

// El servidor web debe escuchar conexiones TCP en un puerto configurable mayor a 1024 y operar de forma continua.

// El servidor web debe ser multi-hilos, creando un hilo independiente por cada solicitud HTTP recibida.

// El servidor web debe interpretar solicitudes HTTP/1.0 usando el método GET y extraer correctamente el recurso solicitado.

// El servidor web debe leer y mostrar por consola la línea de solicitud y los encabezados HTTP recibidos.

// El servidor web debe responder con una estructura HTTP válida (línea de estado, headers y cuerpo) usando CRLF.

// El servidor web debe servir archivos HTML e imágenes (JPG y GIF) determinando correctamente su tipo MIME.

// El servidor web debe manejar recursos inexistentes respondiendo con el código HTTP 404 y un archivo de error.

// El servidor web debe cerrar correctamente sockets y streams sin afectar la atención concurrente de solicitudes.

public class Main {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
    }

    public Main() throws IOException {
        ServerSocket serverSocket = new ServerSocket(5001);
        while (true) {
            System.out.println("Waiting for connection...");
            Socket socket = serverSocket.accept();
            System.out.println("Connected to the server");
            HttpProcess thread = new HttpProcess(socket);
            thread.start();
        }
    }

    public class HttpProcess extends Thread {

        private Socket socket;

        public HttpProcess(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                BufferedWriter bw = new BufferedWriter(
                        new OutputStreamWriter(os));

                BufferedOutputStream out = new BufferedOutputStream(os);

                // Leo lo que me mandó el cliente
                String linea = br.readLine();
                if (linea == null)
                    return;

                // Extrae el nombre del archivo de la línea de solicitud.
                StringTokenizer partesLinea = new StringTokenizer(linea);
                String method = partesLinea.nextToken();
                String nombreArchivo = partesLinea.nextToken();
                

                while ((linea = br.readLine()) != null && !linea.isEmpty()) {
                    System.out.println(linea);
                }

                if (nombreArchivo.equals("/")) {
                    nombreArchivo = "resources/index.html";
                }else {
                    nombreArchivo = "resources" + nombreArchivo;
                }
                //nombreArchivo = "resources" + nombreArchivo;

                

                InputStream inputStream = ClassLoader.getSystemResourceAsStream(nombreArchivo);
                
                
                String lineaDeEstado;
                String lineaHeader;

                if (inputStream != null) {
                    //File file = new File(ClassLoader.getSystemResource(nombreArchivo).toURI());
                    byte[] data = inputStream.readAllBytes();
                    lineaDeEstado = "HTTP/1.0 200 OK\r\n";
                    lineaHeader = "Content-type: " + getMimeType(nombreArchivo) + "\r\n";

                    // String payload = "<html><body>Hola mundo</body></html>";
                    // Respondo al cliente
                    enviarString(lineaDeEstado, out);
                    enviarString(lineaHeader, out);
                    enviarString("Content-Length: " + data.length + "\r\n", out);
                    enviarString("\r\n", out);
                    enviarBytes(inputStream, out);
                    inputStream.close();
                    out.write(data);
                    out.flush();
                } else {
                    InputStream errorStream = ClassLoader.getSystemResourceAsStream("resources/error.html");
                    //File errorfile = new File(ClassLoader.getSystemResource("error.html").toURI());
                    byte[] errorfile = errorStream.readAllBytes();
                    lineaDeEstado = "HTTP/1.0 404 Not Found\r\n";
                    lineaHeader = "Content-type: text/html\r\n";
                    enviarString(lineaDeEstado, out);
                    enviarString(lineaHeader, out);
                    enviarString("Content-Length: " + errorfile.length + "\r\n", out);
                    enviarString("\r\n", out);
                    enviarBytes(errorStream, out);
                    errorStream.close();
                    out.write(errorfile);
                    out.flush();
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        private static void enviarString(String line, OutputStream os) throws IOException{
            os.write(line.getBytes(StandardCharsets.UTF_8));
        }

        private void enviarBytes(InputStream fis, OutputStream os) throws IOException {
            byte[] buffer = new byte[1024];
            int bytes;
            while ((bytes = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytes);
            }
        }

        private String getMimeType(String name) {
            if (name.endsWith(".html"))
                return "text/html";
            if (name.endsWith(".jpg") || name.endsWith(".jpeg"))
                return "image/jpeg";
            if (name.endsWith(".gif"))
                return "image/gif";
            return "application/octet-stream";
        }

    }
}