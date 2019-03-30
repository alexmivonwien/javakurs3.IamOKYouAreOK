package de.kevcodez.system;

/**
 * Enum f�r die m�glichen {@link SocialAuthProvider}<br>
 * <br>
 * Der Provider l�sst sich �ber die <code>toString()</code>-Methode abrufen.
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