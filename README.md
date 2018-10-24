moving on...
will be adding multiple sessions to query data more efficiently (done concurrently).

approach #1:
-create field in table that specifies id of previous stock entry
-store head entry id (like a linked list!)
-time complexity: O(mlogn) where m is number of entries and n is number of rows in table

# screener
a web app to provide stock filters by certain technical requirements

### Controllers
![Controllers](/controllers.png)

### Watchlist
![Watchlist](/watchlist.png)

### Quote
![Quote](/quote.png)
