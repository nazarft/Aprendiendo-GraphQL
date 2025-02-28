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
Una vez instalada la dependencia, tendremos una ruta dedicada exclusivamente a colocar nuestros ficheros de **graphql**.

Para incluir GraphQL en un proyecto de java es tan sencillo como crear un archivo en nuestra estructura de carpetas cuya extensión sea *.graphqls*.
![image](https://github.com/user-attachments/assets/abdbe676-44ed-46da-a826-d091619032f7)

Ahora lo siguiente es que nuestro modelo de Java sea un reflejo de nuestro tipo en GraphQL:

![image](https://github.com/user-attachments/assets/95da048a-498a-48d0-834b-db821058c02e)

Si nos fijamos, nuestros tipos en graphql lucen así:

```graphql
type Book{
    id:ID
    title:String
    author:Author
}
```
Y en java(usando JPA):

```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(Integer id, String title) {
        this.id = id;
        this.title = title;
    }

    public Book(String title) {
        this.title = title;
    }

    public Book() {
    }

    public Book(Integer id, String title, Author author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}

```

Es muy importante que nuestro controlador reflejen en el nombre de los métodos el query que hemos indicado en nuestro archivo *graphql*. Por ejemplo:

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
y el método findById:

```java
@QueryMapping
    public Book bookById(@Argument Integer id) {
        return bookRepository.findById(id).orElse(null);
    }
```
Si tenemos otro nombre en el método, podemos indicarlo así:

```java
@QueryMapping(name = "findBookById")
    public Book bookById(@Argument Integer id) {
        return bookRepository.findById(id).orElse(null);
    }
```

MUY IMPORTANTE!: usar la anotación **@QueryMapping** de Graphql.
MUY IMPORTANTE: agregar @Argument para definir el argumento que pasamos por el query.

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

## Postman

Si no queremos usar la interfaz gráfica y preferimos usar herramientas como *Postman*, es tánf facil como indicar el tipo de consulta:
<img width="214" alt="image" src="https://github.com/user-attachments/assets/a34a31ac-1183-4be6-a6fe-1e2a52e49bf9" />

## Columnas con referencias a otras tablas

Imaginemos que nuestro Book contiene un Author, como haríamos la consulta para que podamos recoger esa información?

En primer lugar creamos el tipo en el schema de graphql:

```graphql
type Author{
    id:ID
    name:String
}
```
Lo reflejamos en nuestro modelo de java:

```java
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;

    public Author() {
    }

    public Author(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
Lo referenciamos en nuestro type Book:

```graphql
type Book{
    id:ID
    title:String
    author:Author
}
```
Y por último, indicamos en el controlador que "mapee" la entidad de Author:

```java
@SchemaMapping
    public Author author(Book book) {
        return authorRepository.findById(book.getAuthor().getId()).orElse(null);
    }
```

## CREATE Y UPDATE

Si queremos hacer el CREATE y UPDATE es tan sencillo cómo crear "mutaciones". Las mutaciones se crean de esta forma:

### CREATE

```graphql
type Mutation{
    addBook(book:BookInput):Book
```

El *input* será el modelo que pasaremos como entrada, sería un modelo de datos dedicado a la introducción de los parámetros que usaremos para crear el libro.

Lo indicamos en nuestro schema:

```graphql
input BookInput{
    title:String
    authorId:ID
}
```
Creamos el método en el controlador:

```
@MutationMapping
    Book addBook(@Argument BookInput book){
        Author author = authorRepository.findById(book.authorId()).orElse(null);
        Book b = new Book(book.title(), author);
        return bookRepository.save(b);
    }
```
IMPORTANTE: agregar la anotación **@MutationMapping**.

### UPDATE

Sería muy similar, creamos la mutación:

```graphql
type Mutation{
    updateBook(id:ID,book:BookInput):Book
}
```

Lo reflejamos en nuestro controlador:

```java
@MutationMapping
    Book updateBook(@Argument Integer id, @Argument BookInput book) {
        Author author = authorRepository.findById(book.authorId()).orElse(null);
        Book b = bookRepository.findById(id).orElse(null);
        b.setTitle(book.title());
        b.setAuthor(author);
        return bookRepository.save(b);
    }
```
**IMPORTANTE**: esta vez no hace falta un *Input* ya que únicamente será necesario el Id del libro y el nuevo libro para actualizar.



Para obtener más información sobre los queries y ejecutar consultas lo mejor es acceder a la documentación oficial: https://graphql.org/learn/schema/





