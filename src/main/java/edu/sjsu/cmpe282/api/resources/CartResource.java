package edu.sjsu.cmpe282.api.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.sjsu.cmpe282.domain.User;
import edu.sjsu.cmpe282.dto.CartDao;

@Path("/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

	CartDao cartdao = new CartDao();
	
	@POST
	@Path("/viewItemsInCart")
	public String viewItemsInCart(String emailID)
	{
		
		return cartdao.viewItemsInCart(emailID);
	}
	@POST
	@Path("/addToCart")
	public String addToCart(String cartDetails)
	{
			
		return cartdao.updateCart(cartDetails);
		
	}
	
	@POST
	@Path("/removeFromCart")
	public String removeFromCart(String cartItem)
	{
			
		return cartdao.removeFromCart(cartItem);
		
	}
}
