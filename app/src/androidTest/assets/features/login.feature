Feature: Login

	Users must sign in with their Focus network credentials in order to gain
	access to the rest of the app. A user who has signed in on a device once
	before should ideally never be required to enter his/her credentials ever
	again on that device.

	@login
    Scenario: Successful login
    	
        Given John has an account
        When he logs in
        Then he should see his grades