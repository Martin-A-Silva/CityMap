# CityMap

For this app the main challenge was to filter through a huge list of cities contained in a JSON
array

My first approach was using [Tries](https://1gravityllc.medium.com/trie-kotlin-50d8ae041202), a tree
structure optimized for string prefix searching.
However, this approach had low performance since it needs a lot of memory, OutOfMemoryExceptions
happened when using the layout inspector.

Thus my second and final approach was to parse the JSON into a local Room database, which would also
enable fast searching thanks to name indexing, more memory friendly, and being able to store which
cities
were marked as favorite.

Another challenge was the Map that had to show the selected city.
At first I tried using OpenStreetMaps since it wouldn't need an API KEY, and it could be implemented
via [MapCompose](https://github.com/p-lr/MapCompose). However this
needed [Raster Tile Providers](https://wiki.openstreetmap.org/wiki/Raster_tile_providers)
which are free but slow.
Since I was not happy with it's performance, I ended up
using [Google Maps for Compose](https://github.com/googlemaps/android-maps-compose)
with the Secrets Gradle plugin to conveniently handle the API KEY. It works out-of-the-box and the
API KEY
is free of charge for 90 days anyway.

I opted for Dagger/Hilt for dependency injection since I'm not familiar with Koin, it helps with
unit testing,
and for a small app it gets the job done pretty well.

