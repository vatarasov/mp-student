package ru.vtarasov.mp.student;

import java.net.URI;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

/**
 *
 */
@Path("/student")
@Singleton
public class StudentController {
    @Inject
    private StudentRegistrationService service;

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String id) {
        Student student = service.find(id);
        if (student == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(student.getName()).build();
    }

    @POST
    public Response post(@FormParam("name") String name) {
        Student student = service.register(name);
        return Response.created(UriBuilder.fromResource(getClass()).path(student.getId()).build()).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        Student student = service.find(id);
        if (student == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        service.unregister(student);
        return Response.ok().build();
    }
}
