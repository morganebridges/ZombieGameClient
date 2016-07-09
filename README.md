# ZombieGameClient
Android client for our zombie location game.

Design Decisions

API calls: 
Userservice Singleton for HTTP
cron service singleton for HTTP


Any call to the api will contain fresh data on a user's location.

A cron will execute on a determined interval to refresh server data on a user's state.

User actions will trigger an API call and reset the timer for the cron.

We will exploit all opportunities to pass peripheral data when an API call needs to be made for another reason.


Connectivity business logic:

If there is a lack of communication from any client node for a set amount of time, we need to

fdsafdf


