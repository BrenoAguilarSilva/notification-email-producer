package io.zact.zops.resource;

import io.zact.zops.dto.NotificationEmailDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProducerResource
 * <p>
 * This class is responsible for handling all HTTP requests related to producer operations.
 * It is a RESTful resource that can be accessed through the path "/api/v1/Notification".
 * It is responsible for handling all CRUD operations related to Notification entity.
 * It is also responsible for handling all exceptions related to Notification entity.
 * It is also responsible for handling all security related to Notification entity.
 * <p>
 */
@Path("/api/v1/notification")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Notification", description = "Notification operations")
public class ProducerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerResource.class);

    @Inject
    @Channel("sendEmail")
    Emitter<NotificationEmailDTO> emailEmitter;

    /**
     * This method is used to send a new email
     *
     * @param notificationEmailDTO - A JSON object with all necessary fields to send a new email
     * @return Response with the confirmation send email
     */
    @POST
    @Path("email/send")
    @Operation(summary = "send a new email", description = "send a new email")
    @APIResponse(responseCode = "200", description = "Send a new email successfully")
    @APIResponse(responseCode = "500", description = "Send a new email Failed")
    public Response sendEmailToQueue(@Parameter(description = "A JSON object with all necessary fields to send a new email") NotificationEmailDTO notificationEmailDTO){
        try{
            emailEmitter.send(notificationEmailDTO);
            return Response.ok().entity("E-mail colocado na fila para envio.").build();
        } catch (Exception e){
            LOGGER.error("Failed to send email to queue", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
