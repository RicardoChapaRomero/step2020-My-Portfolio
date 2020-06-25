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

  /** !Array<Objects> */
  /** @type {{imgSrc: string, description: string, altText: string }}  */
  const GALLERY_ITEMS = [
    {
      imgSrc: "food_picture.jpg",
      description: "My first time eating at Toshi Tiger near the university. The flavor changed my life forever.",
      altText: "A picture of a table with a plate of noodles with meat and vegetables on its top with a plate of bread with shrimp inside it."
    },
    {
      imgSrc: "canada_picture.jpg",
      description: "A family vacation in Canada. Who would have thought that they have amazing views.",
      altText: "An amazing view of the Parlament of Canada and Ottawa River taken from Major's Hill Park."
    },
    {
      imgSrc: "monterrey_picture.JPG",
      description: "An amazing sunset from my city Monterrey!",
      altText: "A beautiful sunset with a deep blue sky with a mix of orange flaming rays of light over Monterrey, Mexico"
    },
    {
      imgSrc: "building_picture.JPG",
      description: "Really appreciate good architecture.",
      altText: "A gothic styled building painted of orange with green roofs on Quebec City"
    },
    {
      imgSrc: "firstday_picture.jpg",
      description: "My first day as a Google STEP intern!",
      altText: "Me smiling with a top knot, wearing a purple shirt with the label 'Hacker', with my Google's Chromebook in front of me"
    },
    {
      imgSrc: "highview_picture.jpg",
      description: "I get really excited when I have views like this.",
      altText: "A view of Montreal from the top of the Olympic Stadium"
    }
  ];

function getRandomImage() {

  // Pick a random gallery item
  const singleGalleryItem = GALLERY_ITEMS[Math.floor(Math.random() * GALLERY_ITEMS.length)];

  // Insert image to the page
  const pictureContainer = document.getElementById('random_picture_container');
  pictureContainer.src = './images/' + singleGalleryItem.imgSrc;
  pictureContainer.alt = singleGalleryItem.altText;

  // Insert image description to the page
  const descriptionContainer = document.getElementById('picture_description_container');
  descriptionContainer.innerText = singleGalleryItem.description;
}
