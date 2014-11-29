package edu.sjsu.cmpe282.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.sjsu.cmpe282.dto.AdminDao;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminResource {
	AdminDao admindao = new AdminDao();
	
	@POST
	@Path("/addCatalog")
	public String addCatalog(String catalogDetails)
	{
		
		return admindao.addCatalog(catalogDetails);
		
	}
	
	
	@POST
	@Path("/addProduct")
	public String addProduct(String productDetails)
	{
		
		return admindao.addProduct(productDetails);
		
	}

}
