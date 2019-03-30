package de.kevcodez.system;

/**
 * Enum für die möglichen {@link SocialAuthProvider}<br>
 * <br>
 * Der Provider lässt sich über die <code>toString()</code>-Methode abrufen.
 * 
 * @author kevcodez.de
 *
 */
public enum SocialAuthProvider {

	FACEBOOK("facebook"), TWITTER("twitter"), YAHOO("yahoo"), HOTMAIL("hotmail"), GOOGLE(
			"google"), LINKED_IN("linkedin"), FOUR_SQUARE("foursquare"), GOOGLE_PLUS(
			"googleplus"), INSTAGRAM("instagram"), FLICKR("flickr"), YAMMER(
			"yammer"), MENDELEY("mendeley"), MYSPACE("myspace");

	private final String provider;

	private SocialAuthProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return provider;
	}
}