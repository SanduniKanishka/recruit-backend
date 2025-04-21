# EchoResource Implementation Documentation

## Overview

This document provides a detailed description of the `EchoResource` class implementation in the `recruit-backend` project, a RESTful web application developed using the **Restlet framework**. The `/echo` endpoint processes HTTP POST requests and supports multiple output formats based on the `Content-Type` and `Accept` headers. The endpoint implements the following functionalities:

- **Plain Text Echoing** (Question 1): Echoes the input as plain text when `Content-Type: text/plain` and `Accept: text/plain` (or no `Accept` header) are used.
- **CSV to HTML Conversion** (Question 2): Converts CSV input to an HTML table when `Content-Type: text/csv` and `Accept: text/html` are specified.
- **CSV to Turtle Conversion** (Bonus): Converts CSV input to RDF data in Turtle format, using Dublin Core Terms metadata, when `Content-Type: text/csv` and `Accept: text/turtle` are specified.

## Requirements

### Plain Text Echoing

- **Input**: Any plain text with `Content-Type: text/plain`.
- **Output**: The same text as the input, with `Content-Type: text/plain`.

### CSV to HTML Conversion

- **Input**: CSV data with headers `title,description,created`.
- **Output**: An HTML table where:
    - Each CSV row is a table row (`<tr>`).
    - Each column is a table cell (`<td>`).
    - Empty cells are rendered as ` ` (non-breaking space).

### CSV to Turtle Conversion

- **Input**: CSV data with headers `title,description,created`.
- **Output**: RDF data in Turtle format, mapping CSV columns to Dublin Core Terms properties:
    - `title` → `dcterms:title`
    - `description` → `dcterms:description`
    - `created` → `dcterms:created`

## Dependencies

The implementation relies on the following dependencies, added to `rest-webapp/pom.xml`:

- **Restlet Framework**: For building the RESTful API.
    - `org.restlet.jse:org.restlet:2.4.3`
    - `org.restlet.jse:org.restlet.ext.servlet:2.4.3`
- **Apache Jena**: To support the CSV to Turtle conversion, the Apache Jena library was added.
    - `org.apache.jena:apache-jena-libs:4.10.0` (type: `pom`)

## Implementation Details

The `EchoResource` class is located in `rest-webapp/src/main/java/se/metasolutions/recruit/resources/EchoResource.java`.

### Class Structure

- **Class**: `EchoResource` extends `BaseResource`.
- **Main Method**:
    - `@Post public Representation echo(Representation entity)`: Handles POST requests to `/echo`.
- **Helper Methods**:
    - `csvToHtml(String csvInput)`: Converts CSV to HTML.
    - `csvToTurtle(String csvInput)`: Converts CSV to Turtle (RDF).

### Functionality Breakdown

#### 1. Plain Text Echoing

- If the `Accept` header is not `text/html` or `text/turtle`, or if the input is not CSV, the input is returned as plain text.


#### 2. CSV to HTML Conversion

- Triggered when Content-Type: `text/csv` and Accept: `text/html`.
- Normalizes line endings by replacing literal `\n` with actual line breaks.
- Splits the input into rows using `\n`.
- For each row, splits into columns using , (preserving empty fields with -1 in split).
- Builds an HTML table with each row as a `<tr>` and each column as a `<td>`.

#### 3. CSV to Turtle Conversion

- Triggered when `Content-Type: text/csv` and `Accept: text/turtle`.
- Normalizes line endings.
- Validates the CSV header (`title,description,created`).
- Uses Apache Jena to create an RDF model.
- Maps each CSV row to an RDF resource (e.g., http://example.org/resource/item_1).
- Maps columns to Dublin Core properties:
    - `title` → `dcterms:title`
    - `description` → `dcterms:description`
    - `created` → `dcterms:created`
- Assigns `rdf:type dcterms:Item` to each resource.
- Serializes the RDF model to Turtle format.

## Testing

The implementation was tested using **cURL** to ensure all functionalities work as expected.

### Test 1: Plain Text Echo

- **Command**:
  ```
  curl -X POST http://localhost:8282/echo -H "Content-Type: text/plain" -d "Hello, World!"
  ```

- **Expected Output**:
  ```
  Hello, World!
  ```


### Test 2: CSV to HTML

- **Command**:
  ```
  curl -X POST http://localhost:8282/echo -H "Content-Type: text/csv" -H "Accept: text/html" -d "title,description,created\nImportant document,This is an important document,2022-03-31\nLess important document,,2022-03-31\nLast document,,"
  ```


- **Expected Output**:

  ```
  <table>
    <tr>
      <td>title</td>
      <td>description</td>
      <td>created</td>
    </tr>
    <tr>
      <td>Important document</td>
      <td>This is an important document</td>
      <td>2022-03-31</td>
    </tr>
    <tr>
      <td>Less important document</td>
      <td> </td>
      <td>2022-03-31</td>
    </tr>
    <tr>
      <td>Last document</td>
      <td> </td>
      <td> </td>
    </tr>
  </table>
  ```
### Test 3: CSV to Turtle

- **Command**:
  ```
  curl -X POST http://localhost:8282/echo -H "Content-Type: text/csv" -H "Accept: text/turtle" -d "title,description,created\nImportant document,This is an important document,2022-03-31\nLess important document,,2022-03-31\nLast document,,"
  ```


- **Expected Output**:
  ```
  @prefix dcterms: http://purl.org/dc/terms/ .
  @prefix ex: http://example.org/resource/ .
  
  ex:item_1 a dcterms:Item ;
    dcterms:title "Important document" ;
    dcterms:description "This is an important document" ;
    dcterms:created "2022-03-31" .
  
  ex:item_2 a dcterms:Item ;
    dcterms:title "Less important document" ;
    dcterms:created "2022-03-31" .
  
  ex:item_3 a dcterms:Item ;
    dcterms:title "Last document" .
  ```
### Test 4: Edge Case (Invalid CSV for Turtle)

- **Command**:
  ```
  curl -X POST http://localhost:8282/echo -H "Content-Type: text/csv" -H "Accept: text/turtle" -d "title,info,created\nImportant document,This is an important document,2022-03-31\nLess important document,,2022-03-31\nLast document,,"
  ```


- **Expected Output**:
  ```
  CSV must have title,description,created headers
  ```

## Challenges and Solutions

### Challenge: Malformed HTML Table Output

- **Issue**: During testing, the cURL command with `\n` in the -d argument on Windows resulted in a malformed HTML table because \n was treated as a literal string.
- **Solution**:
  - Updated `csvToHtml` and `csvToTurtle` to normalize line endings:
    ```
    csvInput = csvInput.replace("\\n", "\n");
    ```

## Usage Instruction

- Build the project
  ```
    ./build.sh
  ```
- Run the server
  ```
    ./standalone/jetty/target/dist/bin/rest.bat
  ```
- The server will start on `localhost:8282`.

## Test the endpoint
- Use the cURL commands provided above to test each functionality.
- Ensure the server is running before sending requests.

## Conclusion
The EchoResource implementation successfully meets all requirements, supporting plain text echoing, CSV-to-HTML conversion, and CSV-to-Turtle conversion with RDF and Dublin Core Terms. Thorough testing with cURL ensures the endpoint works as expected, and the code is robust enough to handle common edge cases.