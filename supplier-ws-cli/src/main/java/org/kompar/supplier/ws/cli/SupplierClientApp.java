package org.kompar.supplier.ws.cli;

/** Main class that starts the Supplier Web Service client. */
public class SupplierClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierClientApp.class.getName() + " wsURL");
			return;
		}
		SupplierClient client ;
        String UDDIURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
            client = new SupplierClient(wsURL);
        } else {
        	System.out.println(args[0]+"<- UDDIURL wsName ->"+args[1]);
            UDDIURL = args[0];
            wsName = args[1];
            client = new SupplierClient(UDDIURL,wsName);
        } 

		// Create client
		System.out.printf("Creating client for server at %s%n", wsURL);

		// the following remote invocations are just basic examples
		// the actual tests are made using JUnit

		System.out.println("Invoke ping()...");
		String result = client.ping("client");
		System.out.print("Result: ");
		System.out.println(result);
	}

}
