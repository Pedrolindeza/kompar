package org.magazyn.store.ws.cli;

/** Main class that starts the Supplier Web Service client. */
public class SupplierClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierClientApp.class.getName() + " wsURL");
			return;
		}
		SupplierClient store ;
        String UDDIURL = null;
        String wsName = null; 
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
            store = new SupplierClient(wsURL);
        } else {
        	System.out.println(args[0] + " <- UDDIURL wsName -> " + args[1]);
            UDDIURL = args[0];
            wsName = args[1];
            store = new SupplierClient(UDDIURL,wsName);
        } 

		// the following remote invocations are just basic examples
		// the actual tests are made using JUnit

		System.out.println("Invoke ping()...");
		String result = store.ping("Store");
		
		System.out.println("Result: " + result);
	}

}
