package edu.sjsu.cmpe282.api.resources;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.sjsu.cmpe282.dto.RecommendDao;

@Path("/recommend")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecommendationResource 
{
	RecommendDao recommenddao = new RecommendDao();

	@POST
	@Path("/userbased")
	public String getUserRecommendation(String userid)
	{
		
			return recommenddao.getUserRecommendation(userid);
		
	}

	@POST
	@Path("/itembased")
	public String getItemRecommendation(String userid)
	{
		return recommenddao.getItemRecommendation(userid);
	}

	@POST
	@Path("/newUserbased")
	public String getNewUserRecommendation(String userid)
	{
		return recommenddao.getNewUserRecommendation(userid);
	}
	
}
