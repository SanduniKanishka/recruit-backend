# Java exercise for the backend recruiting process

## Prerequisites

1. Make sure you have a working Java development environment. The Maven configuration of the boilerplate requires at least Java 11.
2. Clone this repository and don't fork as your code would be visible to other candidates in case you choose to push your implementation to a public repository.
3. Compile by executing `./build.sh`.
4. After successful compilation execute `./standalone/jetty/target/dist/bin/rest`. It launches a REST API and listens on port 8282.
5. Test the API by fetching data from the status resource by executing `curl http://localhost:8282/status`.
6. If any of the above fails you need to revisit your development environment or talk to [hannes@metasolutions.se](mailto:hannes@metasolutions.se) (it may be a bug).

## Boilerplate

As you could see as the result of 5. above, the boilerplate is a very basic, but fully functional implementation of a REST API. The boilerplate is built using the [Restlet framework](https://restlet.talend.com/). You may find its documentation of the [resource architecture](https://restlet.talend.com/documentation/user-guide/2.4/core/resource/overview) and the [server tutorial](https://restlet.talend.com/documentation/user-guide/2.4/introduction/first-steps/first-application) useful.

There are only two REST resources: a status resource that attaches to `/status` and a default resource that responds at `/`.

Implementing a new resource involves creating a new class (see `StatusResource.java` as example) and adding it to the router in `RestApplication.java`.

## Exercise

1. Implement a new resource `/echo` that responds with the request body when posting (HTTP POST) to it; it should simply mirror the request back as response.
2. Add content negotiation to the echo resource and convert the payload: if the request is made with MIME type `text/csv` (i.e., `Content-Type: text/csv`), and the response is expected to be delivered in `text/html` (i.e., `Accept: text/html`), then convert from CSV to a simple HTML table. Very basic handling of the CSV payload (use the snippet from the next section) is sufficient, you don't need to use third party libraries to handle advanced CSVs with apostrophes, line breaks, etc.. Only requests with `Content-Type: text/csv` and `Accept: text/html` need to trigger a conversion, all other requests should behave as described in 1. above.

Bonus for the Linked Data experienced:

3. Add support for the output format Turtle (the [RDF Turtle serialization format](https://www.w3.org/TR/turtle/)) to the echo resource (triggered by `Accept: text/turtle`). Use appropriate properties from the [Dublin Core Terms metadata specification](https://www.dublincore.org/specifications/dublin-core/dcmi-terms/) to map the CSV data to Turtle.

### CSV snippet

```
title,description,created
Important document,This is an important document,2022-03-31
Less important document,,2022-03-31
Last document,,
```

## Questions

If anything is unclear or if you get stuck somewhere in the exercise, do not hesitate to contact [Hannes](mailto:hannes@metasolutions.se) for advice.

## Result

Document your implementation in Text or Markdown format and send it along with your code as a tar.gz archive to [Hannes](mailto:hannes@metasolutions.se). You can also send a link to a Git repository.
