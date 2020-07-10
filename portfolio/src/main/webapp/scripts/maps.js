// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Handles the Map API requests
 */

 let map;

// Loads a map in the portfolio
function createMap() {
  const mapDiv = document.getElementById('map');
  const coordinates = new google.maps.LatLng(25.770714, -100.274921);

  const mapSettings = {
    center: coordinates,
    zoom: 16
  };

  map = new google.maps.Map(mapDiv,mapSettings);
  //setMarkers();
}

function setMarkers() {
  //let placesJSON = JSON.parse(places);
  console.log(places);
  // const infowindow = new google.maps.InfoWindow({
  //   content: contentString
  // });


  // const titosLocation = new google.maps.LatLng(25.771075, -100.268585);
  // const marker = new google.maps.Marker({position: titosLocation, map: map});
  // marker.addListener('click', function() {
  //   infowindow.open(map, marker);
  // });
}
