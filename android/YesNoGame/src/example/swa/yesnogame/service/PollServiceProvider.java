package example.swa.yesnogame.service;

/**
 * Factory to provide an instance of the IPollService service.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class PollServiceProvider {

	private static IPollService theInstance = new PollServiceMock();

	// private static IPollService theInstance = new PollService();

	// private static IPollService theInstance = new PollServiceCloud();

	public static IPollService getService() {
		return theInstance;
	}
}
