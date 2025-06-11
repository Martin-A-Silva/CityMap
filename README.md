# CityMap
## Introduction
This app loads a list of cities from a JSON file and displays them in a list, with the ability to
show their location on a map, save them as favorites, filter by string prefix and/or favorites, and
show extra information about the selected city.

## Application challenges
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
API key is free of charge for 90 days anyway.
For handling the API I'm using the Secrets Gradle plugin. You have to create a secrets.properties
file on the root of your project with the following content: MAPS_API_KEY=<your API key>.

I opted for Dagger/Hilt for dependency injection since I'm not familiar with Koin, it helps with
unit testing, and for a small app it gets the job done pretty well.

## Limitations
Since time was of the essence, I had to make some decisions.

- UI tests have been left out, but I would have chosen robolectric, since it doesn't need a
running emulator thanks to the shadow classes, making it more friendly with CI like Jenkins or CircleCI
- Responsiveness when tapping the "show favorites only" button could be better, probably by indexing
by "isFavorite" too, but still in it's current state it's not too bad. Showing a spinner when
toggling the "favorites only" would have been nice though.
- Using UI States and UI Events would have made the code more readable, predictable and testable
- UseCases and a domain layer would make the code more Clean Arch-y, but for a small app they can
be expendable

![Screen Recording 2025-06-10 at 21 18 39](https://github.com/user-attachments/assets/efaec481-536b-4357-be58-268978f0132a)
