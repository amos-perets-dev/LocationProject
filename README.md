# LocationProject

The architecture are MVVM and DI

Using DI in order to inject the objects outside the class.
Using MVVM to speartae the UI/View from the bussines logic and support reactive programing(via RxJava)

Project schema:

Main screen- show the map with pins.

Managers:
  Location - Handle the change location.
  Map - Create the pins on the map.
  Places - Handle the requests to the server

BroadcastReceiver:
  GeofenceBroadcastReceiver - Handle the trigers  

PlacesRepository: Handle the data of the places and the location

Utils:
  PermissionsUtil - Responsible to check the permissions.
  IconUtil - Responsible to create Bitmap

