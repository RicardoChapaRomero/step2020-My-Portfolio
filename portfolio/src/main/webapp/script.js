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
 * Adds a random picture with its description to the page.
 */

function getRandomImage() {
  const pictures_and_description = [
    {
      img_src: "food_picture.jpg",
      description: "My first time eating at a restaurant near the university. The flavor changed my life forever."
    },
    {
      img_src: "canada_picture.jpg",
      description: "A family vacation in Canada. Who would have thought that they have amazing views."
    },
    {
      img_src: "monterrey_picture.JPG",
      description: "An amazing view from my city Monterrey!"
    }
  ];

  // Pick a random Object
  const singlePair = pictures_and_description[Math.floor(Math.random() * pictures_and_description.length)];

  // Insert image to the page
  const pictureContainer = document.getElementById('random_picture_container');
  pictureContainer.src = './images/' + singlePair.img_src;

  // Insert image description to the page
  const descriptionContainer = document.getElementById('picture_description_container');
  descriptionContainer.innerText = singlePair.description;
}
