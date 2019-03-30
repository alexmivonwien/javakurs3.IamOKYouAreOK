package de.kevcodez.system;

import org.brickred.socialauth.SocialAuthConfig;

/**
 * Klasse f�r diverse Systemkonstanten, die sich w�hrend der Laufzeit nicht mehr ver�ndern
 * 
 * @author kevcodez.de
 * @see https://kevcodez.de/index.php/2015/06/socialauth-facebook-login-mit-jsf-2-2-und-cdi/
 * 
 *
 */
public final class Consts
{
  /** Basis-URL unserer Web-Applikation **/
  public static final String BASE_URL = "http://localhost:8080/IamOKYouAreOK";
  
  /** Config f�r das Login in soziale Netzwerke **/
  public static final SocialAuthConfig SOCIAL_AUTH_CONFIG = SocialAuthConfig.getDefault();
  
  static
  {
    try
    {
      SOCIAL_AUTH_CONFIG.load();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  /**
   * Privater Konstruktor, um die Instanziierung zu verhindern
   */
  private Consts(){}
}