package edu.sjsu.cmpe282.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.sjsu.cmpe282.domain.User;

import edu.sjsu.cmpe282.dto.TracksDao;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TracksResource {

	TracksDao dao = new TracksDao();



	@POST
	@Path("/searchByArtistID")
	public String getTracks(String artistID)
	{

		System.out.println("In Resource");
		return dao.getTracks(artistID);



	}
}
