# ¿Qué es GraphQL?

GraphQL es un lenguaje de consulta y un runtime para ejecutar esas consultas sobre tus datos. Fue desarrollado por Facebook y se ha convertido en una alternativa muy popular a las APIs REST tradicionales. Algunas características clave de GraphQL son:

* Consulta Específica: Permite a los clientes pedir exactamente los datos que necesitan, evitando recibir información innecesaria.
  
* Un Único Endpoint: A diferencia de REST, que suele tener múltiples endpoints para distintos recursos, GraphQL opera a través de un único endpoint.
  
* Esquema Fuertemente Tipado: Define un esquema que especifica qué datos y operaciones están disponibles, lo que facilita la validación y el mantenimiento de la API.
  
* Consultas Anidadas y Relaciones Complejas: Permite solicitar datos relacionados en una sola consulta, optimizando la comunicación entre cliente y servidor.

# GraphQL VS REST

![image](https://github.com/user-attachments/assets/7baaa72e-7095-4649-8763-3cc94e77959d)

Como se muestra en la imagen, unicamente se necesita un endpoint para las consultas que haga el cliente.

# Incluir GraphQL en un proyecto de Java

Lo primero de todo será incluir la dependencia de GraphQL en nuestro pom:

```java
org.springframework.boot:spring-boot-starter-graphql
```

Para incluir GraphQL en un proyecto de java es tan sencillo como crear un archivo en nuestra estructura de carpetas cuya extensión sea *.graphqls*.
![image](https://github.com/user-attachments/assets/abdbe676-44ed-46da-a826-d091619032f7)

Ahora lo siguiente es que nuestro modelo de Java sea un reflejo de nuestro tipo en GraphQL:

![image](https://github.com/user-attachments/assets/95da048a-498a-48d0-834b-db821058c02e)

Es muy importante que nuestro controlador reflejen en el nombre de los métodos el query que hemos indicado en nuestro archivo *graphql*(en nuestro proyecto de java
se podria situar en resources/graphql). Por ejemplo:

Nuestro query es:

```graphql
type Query {
    books: [Book]
    bookById(id: Int): Book
}
```
Pues en nuestro controlador, el método que devuelva todos los libros sería:

```java
@QueryMapping
    public List<Book> books() {
        return Book.books;
    }
```
MUY IMPORTANTE!: usar la anotación **@QueryMapping** de Graphql.

## Interfaz gráfica 

GraphQL nos proporciona una interfaz gráfica para poder interactuar con los queries. Para poder usarla será necesario agregar en nuestro **application.properties** la siguiente línea:

```java
#UI for Graphql
spring.graphql.graphiql.enabled=true
```
Entonces nuestro controlador se vería así:

```java
@Controller
public class BookController {
    @QueryMapping
    public List<Book> books() {
        return Book.books;
    }
```

Ahora si ejecutamos nuestro proyecto y accedemos a la ruta *http://localhost:8080/graphiql*:

<img width="696" alt="image" src="https://github.com/user-attachments/assets/cd4696ee-ba2f-4ec3-b5ae-d63584b468b1" />

Para obtener más información sobre los queries y ejecutar consultas lo mejor es acceder a la documentación oficial: https://graphql.org/learn/schema/

## Método findById

Si nos fijamos en el query de GraphQl:

```graphql
type Query {
    books: [Book]
    bookById(id: Int): Book
}
```
Podemos observar que indicamos buscar un libro por id, de tal manera, en nuestro controlador crearemos el método:

```java
 @QueryMapping
    public Book bookById(@Argument  Integer id) {
        return Book.getBookById(id);
    }
```
IMPORTANTE: agregar @Argument para definir el argumento que pasamos por el query

IMPORTANTE: @QueryMapping coge el nombre del query y lo usa si es el mismo nombre en el método, si el nombre fuera distinto bastaría con agregar esta línea:

```java
@QueryMapping(name = "libroPorId")
```

Y nuestra clase de Book sería:

```java
public static Book getBookById(Integer id) {
        return books.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }
```
