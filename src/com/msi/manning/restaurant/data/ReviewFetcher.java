package com.msi.manning.restaurant.data;

import android.util.Log;

import com.msi.manning.restaurant.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.*;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Use Yelp web service with specified criteria to obtain Review data.
 * 
 * @author Zhiguang Xu
 */
public class ReviewFetcher {

	private static final String CLASSTAG = ReviewFetcher.class.getSimpleName();
	// private static final String QBASE =
	// "http://navitend.com/androidinaction/reviews.php?type=restaurant";
	// private static final String QD_PREFIX = "&description=";
	// private static final String QL_PREFIX = "&location=";
	// private String query;

	private OAuthService service;
	private Token accessToken;
	private OAuthRequest request;

	private final ArrayList<Review> reviews;

	/**
	 * Construct ReviewFetcher with location, description, rating, and paging
	 * params.
	 * 
	 * @param location
	 * @param description
	 * @param rating
	 * @param start
	 * @param numResults
	 */
	public ReviewFetcher(String loc, String des, String rat, int start,
			int numResults) {
		// Update tokens here from Yelp developers site, Manage API access.
		// http://www.yelp.com/developers/manage_api_keys
		String consumerKey = "0Zn9dxuhMUyXoNB5WErI1w";
		String consumerSecret = "nMYqpY1ZqSelxzGCT69WnoswNjU";
		String token = "BVSsPFNspS-GWA3OmeeDreIGqK-KTFST";
		String tokenSecret = "TijS_myGG1-EIlzNmY8LOxepETI";

		service = new ServiceBuilder().provider(YelpApi2.class)
				.apiKey(consumerKey).apiSecret(consumerSecret).build();
		accessToken = new Token(token, tokenSecret);

		Log.v(Constants.LOGTAG, " " + ReviewFetcher.CLASSTAG + " location = "
				+ loc + " description = " + des + " rating = " + rat
				+ " start = " + start + " numResults = " + numResults);

		String location = loc;
		String description = des;
		String rating = rat;

		// urlencode params
		try {
			if (location != null) {
				location = URLEncoder.encode(location, "UTF-8");
			}
			if (description != null) {
				description = URLEncoder.encode(description, "UTF-8");
			}
			if (rating != null) {
				rating = URLEncoder.encode(rating, "UTF-8");
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		// build query
		// this.query = ReviewFetcher.QBASE;
		// if ((location != null) && !location.equals("")) {
		// this.query += (ReviewFetcher.QL_PREFIX + location);
		// }
		// if ((description != null) && !description.equals("ANY")) {
		// this.query += (ReviewFetcher.QD_PREFIX + description);
		// }

		// build Yelp request
		String term = "Restaurant";
		request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
		if ((description != null) && !description.equals("ANY")) {
			request.addQuerystringParameter("term", description + " " + term);
		} else {
			request.addQuerystringParameter("term", term);
		}
		if ((location != null) && !location.equals("")) {
			request.addQuerystringParameter("location", location);
		}
		if ((rating != null) && !rating.equals("")) {
			request.addQuerystringParameter("rating", rating);
		}

		request.addQuerystringParameter("limit", "20");
		service.signRequest(accessToken, request);

		Log.v(Constants.LOGTAG, " " + ReviewFetcher.CLASSTAG + " request - "
				+ this.request);

		reviews = new ArrayList<Review>();
	}

	/**
	 * Parse JSON.
	 * 
	 * @return
	 */
	private ArrayList<Review> parseReviews(String resp) {
		try {
			JSONObject mainJson = new JSONObject(resp);
			JSONArray businesses = mainJson.getJSONArray("businesses");
			for (int i = 0; i < businesses.length(); i++) {
				JSONObject business = businesses.getJSONObject(i);
				System.out.println("name: " + business.getString("name"));
				System.out.println("rating: " + business.getString("rating"));
				System.out.println("phone: " + business.getString("phone"));
				JSONArray address = business.getJSONObject("location")
						.getJSONArray("display_address");
				String address_string = "";
				for (int j = 0; j < address.length(); j++)
					address_string = address_string + address.getString(j)
							+ " ";
				System.out.println("address: " + address_string);
				System.out.println();
				
				Review review = new Review();
				review.name = business.getString("name");
				review.rating = business.getString("rating");
				review.phone = business.getString("phone");
				review.location = address_string;
				reviews.add(review);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return reviews;
	}

	/**
	 * Call Yelp.
	 * 
	 * @return
	 */
	public ArrayList<Review> getReviews() {
		long startTime = System.currentTimeMillis();
		ArrayList<Review> results = null;

		try {
			Response response = request.send();
			String resp = response.getBody();

			// after receiving response, get it parsed
			results = parseReviews(resp);
		} catch (Exception e) {
			Log.e(Constants.LOGTAG, " " + ReviewFetcher.CLASSTAG, e);
		}
		long duration = System.currentTimeMillis() - startTime;
		Log.v(Constants.LOGTAG, " " + ReviewFetcher.CLASSTAG
				+ " send request and parse reviews duration - " + duration);
		return results;
	}
}
