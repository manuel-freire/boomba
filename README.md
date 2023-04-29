# LA BOOMBA

## PENDING

** [ X ] ** Websockets palabra correcta

** [ ] ** Websockets configuración juego

** [ ] ** Websockets time's up

** [ X ] ** Websockets next turn (vista)

** [ ] ** Websockets chat

** [ ] ** Websockets fin partida

** [ ] ** Reports

** [ ] ** Profile

## DB

![DB](https://user-images.githubusercontent.com/56733112/223422199-9e879b59-2e70-4b7f-ad1a-63f63a39603c.png)

## Vistas principales de la aplicación web

![Vista de Inicio | Home View](https://user-images.githubusercontent.com/56733112/219961382-54e259ed-a8ce-4464-a9b5-1a8fb19eb289.jpg)
![Vista de Lobby | Lobby View](https://user-images.githubusercontent.com/56733112/219961733-8644884e-3d85-4288-8fdc-69dc827e0095.jpg)
![Vista de Juego | Game View ](https://user-images.githubusercontent.com/56733112/219961770-44a84ed5-2e59-45c3-8e8d-cd9317ad4865.jpg)
![Vista de Observador | Observer View](https://user-images.githubusercontent.com/56733112/219961786-72505f71-0c44-4759-8265-37f2e1a5b198.jpg)
![Vista de Resumen | Recap View](https://user-images.githubusercontent.com/56733112/219961804-cdce3c92-ac83-4280-b050-e4a994ca94a3.jpg)
![vista de Registro | Sign Up View](https://user-images.githubusercontent.com/56733112/219961965-21b373e7-df5d-4f5c-bfb9-3c10722dd33c.jpg)
![Vista de Perfil | Profile View](https://user-images.githubusercontent.com/56733112/219961946-43fea094-4bb7-43e0-8971-1d54126c67df.jpg)
![Vista de Admin | Admin View](https://user-images.githubusercontent.com/56733112/219961986-e3cb5c97-4acb-4201-8d30-a044b7ecac32.jpg)

## Contenido de la plantilla

- en [src/main/java/es/ucm/fdi/iw](https://github.com/manuel-freire/iw/tree/main/plantilla/src/main/java/es/ucm/fdi/iw) están los ficheros de configuración-mediante-código de la aplicación (ojo porque en otro sitio está el fichero principal de configuración-mediante-propiedades, [application.properties](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/application.properties)):

    * **AppConfig.java** - configura LocalData (usado para gestionar subida y bajada de ficheros de usuario) y fichero de internacionalización (que debería llamarse `Messages_XX.properties`, donde `XX` es un código como `es` para español ó `en` para inglés; y vivir en el directorio [resources](https://github.com/manuel-freire/iw/tree/main/plantilla/src/main/resources).
    * **IwApplication.java** - punto de entrada de Spring Boot
    * **IwUserDetailsService.java** - autenticación mediante base de datos. Referenciado desde SecurityConfig.java. La base de datos se inicializa tras cada arranque desde el [import.sql](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/import.sql), aunque tocando [application.properties](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/application.properties) puedes hacer que se guarde y cargue de disco, ignorando el _import_.
    * **LocalData.java** - facilita guardar y devolver ficheros de usuario (es decir, que no forman parte de los fuentes de tu aplicación). Para ello colabora con AppConfig y usa el directorio especificado en [application.properties](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/application.properties)
    * **LoginSuccessHandler.java** - añade una variable de sesión llamada `u` nada más entrar un usuario, con la información de ese usuario. Esta variable es accesible desde Thymeleaf con `${session.user}`, y desde cualquier _Mapping_ de controllador usando el argumento `HttpSession session`, y leyendo su valor vía `(User)session.getAttribute("u")`. También añade a la sesión algo de configuración para websockets (variables `ws` y `url`), que se escriben como JS en las cabeceras de las páginas en el fragmento [head.html](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/templates/fragments/head.html).
    * **SecurityConfig.java** - establece la configuración de seguridad. Modifica su método `configure` para decir quién puede hacer qué, mediante `hasRole` y `permitAll`. 
    * **StartupConfig.java** - se ejecuta nada más lanzarse la aplicación. En la plantilla sólo se usa para inicializar la `debug` a partir del [application.properties](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/application.properties), accesible desde Thymeleaf mediante `${application.debug}`
    * **WebSocketConfig.java** - configura uso de websockets
    * **WebSocketSecurityConfig.java** - seguridad para websockets

- en [src/main/java/es/ucm/fdi/iw/controller](https://github.com/manuel-freire/iw/tree/main/plantilla/src/main/java/es/ucm/fdi/iw/controller) hay 3 controladores:

  * **RootController.java** - para usuarios que acaban de llegar al sitio, gestiona `/` y `/login`
  * **AdminController.java** - para administradores, gestionando todo lo que hay bajo `/admin`. No hace casi nada, pero sólo pueden llegar allí los que tengan rol administrador (porque así lo dice en SecurityConfig.config)
  * **UserControlller.java** - para usuarios registrados, gestionando todo lo que hay bajo `/user`. Tiene funcionalidad útil para construir páginas:
  
    + Un ejemplo de método para gestionar un formulario de cambiar información del usuario (bajo `@PostMapping("/{id}")`)
    + Puede devolver imágenes de avatar, y permite también subirlas. Ver métodos `getPic` (bajo `@GetMapping("{id}/pic")`) y `postPic` (bajo `@PostMapping("{id}/pic")`)
    + Puede gestionar también peticiones AJAX (= que no devuelven vistas) para consultar mensajes recibidos, consultar cuántos mensajes no-leídos tiene ese usuario, y enviar un mensaje a ese usuario (`retrieveMessages`, `checkUnread` y `postMsg`, respectivamente). Esta última función también envía el mensaje via websocket al usuario, si es que está conectado en ese momento.
    
- en [src/main/resources](https://github.com/manuel-freire/iw/tree/main/plantilla/src/main/resources) están los recursos no-de-código-de-servidor, y en particular, las vistas, los recursos web estáticos, el contenido inicial de la BBDD, y las propiedades generales de la aplicación.

  * **static/**  - contiene recursos estáticos web, como ficheros .js, .css, ó imágenes que no cambian
  
    - **js/stomp.js** - necesario para usar STOMP sobre websockets (que es lo que usaremos para enviar y recibir mensajes)
    - **js/iw.js** - configura websockets, y contiene funciones de utilidad para gestionar AJAX y previsualización de imágenes
    - **js/ajax-demo.js** - ejemplos (usados desde [user.html](https://github.com/manuel-freire/iw/blob/main/plantilla/src/main/resources/templates/user.html)) de AJAX, envío y recepción de mensajes por websockets, y previsualización de imágenes

  * **templates/** - contiene vistas, y fragmentos de vista (en `templates/fragments`)
  
    - **fragments/head.html** - para incluir en el `<head>` de tus páginas. Incluída desde  
    - **fragments/nav.html** - para incluir al comienzo del `<body>`, contiene una navbar. *Cambia los contenidos* para que tengan sentido para tu aplicación.    
    - **fragments/footer.html** - para incluir al final del `<body>`, con un footer. *Cambia su contenido visual*, pero ten en cuenta que es donde se cargan los .js de bootstrap, además de `stomp.js` e `iw.js`.
    - **error.html** - usada cuando se producen errores. Tiene un comportamiento muy distinto cuando la aplicación está en modo `debug` y cuando no lo está. 
    - **user.html** - vista de usuario. Debería mostrar información sobre un usuario, y posiblemente formularios para modificarle, pero en la plantilla se usa para demostrar funcionamiento de AJAX y websockets, en conjunción con `static/js/ajax-demo.js`. Deberías, lógicamente, *cambiar su contenido*.
  
  * **application.properties** - contiene la configuración general de la aplicación. Ojo porque ciertas configuraciones se hacen en los ficheros `XyzConfig.java` vistos anteriormente. Por ejemplo, qué roles pueden acceder a qué rutas se configura desde `SecurityConfig.java`.
  * **import.sql** - contiene código SQL para inicializar la BBDD. La configuración inicial hace que la BBDD se borre y reinicialice a cada arranque, lo cual es útil para pruebas. Es posible cambiarla para que la BBDD persista entre arraques de la aplicación, y se ignore el `import.sql`.
    
