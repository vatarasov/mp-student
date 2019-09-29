package ru.vtarasov.mp.student;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;

/**
 *
 */
@Path("/student")
@Singleton
public class StudentController {

    @RequiredArgsConstructor
    private final class StudentNotFoundException extends Exception {}

    @Inject
    private StudentRegistrationService service;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Student get(@PathParam("id") String id) throws StudentNotFoundException {
        return service.find(id).orElseThrow(StudentNotFoundException::new);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Valid Student student) {
        student = service.register(student);
        return Response.created(UriBuilder.fromResource(StudentController.class).path(student.getId()).build()).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) throws StudentNotFoundException {
        Student student = service.find(id).orElseThrow(StudentNotFoundException::new);
        service.unregister(student);
        return Response.ok().build();
    }

    @Provider
    public static class NotFoundMapper implements ExceptionMapper<StudentNotFoundException> {
        @Override
        public Response toResponse(StudentNotFoundException exception) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}
