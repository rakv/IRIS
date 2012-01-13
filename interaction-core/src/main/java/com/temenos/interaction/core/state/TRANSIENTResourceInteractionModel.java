package com.temenos.interaction.core.state;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.NotSupportedCommand;
import com.temenos.interaction.core.command.ResourceGetCommand;
import com.temenos.interaction.core.command.ResourcePostCommand;

/**
 * Defines a Resource Interaction Model for a resource with no state at all.
 * @author aphethean
 */
public class TRANSIENTResourceInteractionModel implements ResourceInteractionModel {

	private final Logger logger = LoggerFactory.getLogger(TRANSIENTResourceInteractionModel.class);

	private String entityName;
	private String resourcePath;
	private CommandController commandController;
		
	public TRANSIENTResourceInteractionModel(String entityName, String resourcePath) {
		this.entityName = entityName;
		this.resourcePath = resourcePath;
		this.commandController = new CommandController(resourcePath);
	}
	
	public String getEntityName() {
		return entityName;
	}

	public String getResourcePath() {
		return resourcePath;
	}
	
	protected CommandController getCommandController() {
		return commandController;
	}

	/**
	 * POST a document to a resource.
	 * @precondition a valid POST command for this resourcePath + id must be registered with the command controller
	 * @postcondition a Response with non null Status must be returned
	 * @invariant resourcePath not null
	 */
    @POST
//    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    @Consumes({MediaType.WILDCARD})
    public Response post( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource resource ) {
    	logger.debug("POST " + resourcePath);
    	assert(resourcePath != null);
		ResourcePostCommand postCommand = (ResourcePostCommand) commandController.fetchStateTransitionCommand("POST", getResourcePath());
    	RESTResponse response = postCommand.post(id, resource);
    	assert (response != null);
    	StatusType status = response.getStatus();
    	assert (status != null);  // not a valid post command
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);
			ResponseBuilder rb = Response.ok(response.getResource()).status(status);
			return HeaderHelper.allowHeader(rb, response).build();
		}
   		return Response.status(status).build();
    }
    
    @GET
    @Consumes({MediaType.WILDCARD})
    public Response getNotImplemented( @Context HttpHeaders headers, @PathParam("id") String id) {
    	logger.debug("GET " + resourcePath);
    	assert(resourcePath != null);
    	ResourceGetCommand getCommand = commandController.fetchGetCommand();
    	RESTResponse response = getCommand.get(id);
    	assert (response != null);
    	StatusType status = response.getStatus();
		assert (status != null);  // not a valid get command
		if (status.getFamily() == Response.Status.Family.SUCCESSFUL) {
			assert(response.getResource() != null);
			ResponseBuilder rb = Response.ok(response.getResource()).status(status);
			return HeaderHelper.allowHeader(rb, response).build();
		}
		return Response.status(status).build();
    }

    @PUT
    @Consumes({MediaType.WILDCARD})
    @Produces({MediaType.WILDCARD})
    public Response putNotImplemented( @Context HttpHeaders headers, @PathParam("id") String id, String resource ) {
    	logger.debug("PUT " + resourcePath + " " + headers.getMediaType());
    	// TODO, status should  be not allowed as the TRANSIENT resource can only have a GET or POST method, return not implemented if the TRANSIENT resource does not have a GET or POST
   		return Response.status(NotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, com.temenos.interaction.core.media.hal.MediaType.APPLICATION_HAL_XML})
    public Response putEntityNotImplemented( @Context HttpHeaders headers, @PathParam("id") String id, EntityResource resource ) {
    	logger.debug("PUT " + resourcePath + " " + headers.getMediaType());
    	// TODO, status should  be not allowed as the TRANSIENT resource can only have a GET or POST method, return not implemented if the TRANSIENT resource does not have a GET or POST
   		return Response.status(NotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Consumes({MediaType.WILDCARD})
    public Response deleteNotImplemented( @Context HttpHeaders headers, @PathParam("id") String id) {
    	logger.debug("DELETE " + resourcePath);
   		return Response.status(NotSupportedCommand.HTTP_STATUS_NOT_IMPLEMENTED).build();
    }

	@Override
	public Response options(String id) {
    	logger.debug("OPTIONS " + resourcePath);
    	return Response.status(Status.NOT_FOUND).build();
	}

}
