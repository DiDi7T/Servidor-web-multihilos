# Servidor-web-multihilos


El servidor pretende cumplir con:



El servidor web debe escuchar conexiones TCP en un puerto configurable mayor a 1024 y operar de forma continua.

El servidor web debe ser multi-hilos, creando un hilo independiente por cada solicitud HTTP recibida.

El servidor web debe interpretar solicitudes HTTP/1.0 usando el método GET y extraer correctamente el recurso solicitado.

El servidor web debe leer y mostrar por consola la línea de solicitud y los encabezados HTTP recibidos.

El servidor web debe responder con una estructura HTTP válida (línea de estado, headers y cuerpo) usando CRLF.

El servidor web debe servir archivos HTML e imágenes (JPG y GIF) determinando correctamente su tipo MIME.

El servidor web debe manejar recursos inexistentes respondiendo con el código HTTP 404 y un archivo de error.

El servidor web debe cerrar correctamente sockets y streams sin afectar la atención concurrente de solicitudes.
