YesNoGame
=========

This is an example Android application, presented at the Siemens SWA workshop #3 run 22.
Read the short description of the game in the attached pdf-file. 
We will create an application that has 4 screens and 5 android "activities", where the users
can open polls, vote on polls and watch the results of polls. On the way it is shown
how to consume a RESTful service.

Rob an me supplied 3 implementations for the service.

a.) PollServiceMock  : Use this if you want to understand how to asyncronously consume services.
b.) PollService      : Set up the PHP server part of the example and see how it works in real life. Dont forget the .htaccess file or set up your Apache or nginx accordingly. (See flight framework website for an example.)
c.) PollServiceCloud : Set up a REST service using .NET and WCF (sources will be supplied by Rob later on).
                       In the workshop Rob showed us, how to put it in the Amazon cloud.
