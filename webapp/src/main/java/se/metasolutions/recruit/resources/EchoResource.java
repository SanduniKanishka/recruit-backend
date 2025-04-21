package se.metasolutions.recruit.resources;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.io.StringWriter;

public class EchoResource extends BaseResource{

    @Post
    public Representation echo(Representation entity) throws Exception{
        String acceptHeader = getRequest().getHeaders().getFirstValue("Accept", true);
        MediaType acceptMediaType = acceptHeader != null ? MediaType.valueOf(acceptHeader) : MediaType.TEXT_PLAIN;

        MediaType contentType = entity.getMediaType() != null ? entity.getMediaType() : MediaType.TEXT_PLAIN;

        String input = entity.getText();

        if (MediaType.TEXT_TURTLE.equals(acceptMediaType)){
            if (MediaType.TEXT_CSV.equals(contentType)){
                return csvToTurtle(input);
            }
        } else if (MediaType.TEXT_HTML.equals(acceptMediaType) && MediaType.TEXT_CSV.equals(contentType)){
            return csvToHtml(input);
        }

        return new StringRepresentation(input, MediaType.TEXT_PLAIN);
    }

    private Representation csvToHtml(String csvInput) {
        // Split the CSV into lines
        csvInput = csvInput.replace("\\n", "\n");
        String[] lines = csvInput.split("\n");
        StringBuilder html = new StringBuilder("<table>\n");

        for (int  i = 0; i < lines.length; i++){
            String[] values = lines[i].split(",", -1);
            html.append(" <tr>\n");
            for (String value : values){
                String displayValue = value.trim().isEmpty() ? " " : value.trim();
                html.append(" <td>").append(displayValue).append("</td>\n");
            }
            html.append(" </tr>\n");
        }
        html.append("</table>");
        return new StringRepresentation(html.toString(), MediaType.TEXT_HTML);
    }

    private Representation csvToTurtle(String csvInput){
        csvInput = csvInput.replace("\\n", "\n");
        String[] lines = csvInput.split("\n");
        if (lines.length < 1){
            return new StringRepresentation("Invalid CSV Input", MediaType.TEXT_TURTLE);
        }

        String[] headers = lines[0].split(",", -1);
        if ((headers.length !=3 || !headers[0].trim().equalsIgnoreCase("title")) ||
            !headers[1].trim().equalsIgnoreCase("description") || !headers[2].trim().equalsIgnoreCase("created") ){
            return new StringRepresentation("CSV must have title, description, created headers.", MediaType.TEXT_TURTLE);
        }

        Model model = ModelFactory.createDefaultModel();

        String dcterms = "http://purl.org/dc/terms/";
        String ex = "http://example.org/resource/";
        model.setNsPrefix("dcterms", dcterms);
        model.setNsPrefix("ex", ex);

        for (int i=1; i<lines.length; i++){
            String[] values = lines[i].split(",",-1); //-1 to keep empty trailing fields
            if (values.length != 3) continue;

            String title = values[0].trim();
            String description = values[1].trim();
            String created = values[2].trim();

            String resourceUri = ex + "item_" + i;
            Resource resource = model.createResource(resourceUri);
            resource.addProperty(RDF.type, model.createResource(dcterms + "Item"));

            // Add Dublin Core Properties
            if (!title.isEmpty()){
                resource.addProperty(model.createProperty(dcterms + "title"), title);
            }
            if (!description.isEmpty()){
                resource.addProperty(model.createProperty(dcterms + "description"), description);
            }
            if (!created.isEmpty()){
                resource.addProperty(model.createProperty(dcterms + "created"), created);
            }
        }

        // Serialize the model to Turtle
        StringWriter writer = new StringWriter();
        model.write(writer, "TURTLE");
        return new StringRepresentation(writer.toString(), MediaType.TEXT_TURTLE);
    }
}
