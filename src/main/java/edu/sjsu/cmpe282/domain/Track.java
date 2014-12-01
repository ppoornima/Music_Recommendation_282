package edu.sjsu.cmpe282.domain;

import java.io.Serializable;

public class Track  implements Serializable
{

	public String trackid;
	public String userid;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTrackid() {
		return trackid;
	}
	public void setTrackid(String trackid) {
		this.trackid = trackid;
	}
	public String getAlbumid() {
		return albumid;
	}
	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}
	public String getArtistid() {
		return artistid;
	}
	public void setArtistid(String artistid) {
		this.artistid = artistid;
	}
	public String getGenreid() {
		return genreid;
	}
	public String getRatings() {
		return ratings;
	}
	public void setRatings(String ratings) {
		this.ratings = ratings;
	}
	public String ratings;

	public void setGenreid(String genreid) {
		this.genreid = genreid;
	}
	public String albumid;
	public String artistid;
	public String genreid;

}

