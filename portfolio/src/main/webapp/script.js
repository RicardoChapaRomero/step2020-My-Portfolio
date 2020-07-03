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

/** @const {!Array<{imgSrc: string, description: string, altText: string }>}  */
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
    description: "An amazing sunset over my city Monterrey!",
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

const TIME_INTERVAL = 5000; // Constant time of 5 seconds
let isCycling = false; // Bool to track if a cycle is active
let intervalState = null; // Variable to store the setInterval() status
let galleryItemsIndex = 0; // Counter to keep track of the display of the images

function onClickImageCyclerButton() {
  // Change status of the cycle on every click
  isCycling = !isCycling
  const randomButtonContainer = document.querySelector('.random_picture_button');

  if(isCycling) {
    //Start the random image cycler
    intervalState = setInterval(setRandomImage,TIME_INTERVAL);
    randomButtonContainer.textContent = 'Click to stop the random image cycler';
  } else {
    //Stop the random image cycler
    randomButtonContainer.textContent = 'Click to start the random image cycler';
    clearInterval(intervalState);
    intervalState = null;
  }
}

function setRandomImage() {
  // Condition to check when new cycle has to begin
  if(galleryItemsIndex === GALLERY_ITEMS.length) {
    shuffleGalleryItems();
    galleryItemsIndex = 0;
  }

  let singleGalleryItem = GALLERY_ITEMS[galleryItemsIndex];

  // Insert image to the page
  const pictureContainer = document.getElementById('random_picture_container');
  pictureContainer.src = './images/' + singleGalleryItem.imgSrc;
  pictureContainer.alt = singleGalleryItem.altText;

  // Insert image description to the page
  const descriptionContainer = document.getElementById('picture_description_container');
  descriptionContainer.innerText = singleGalleryItem.description;

  galleryItemsIndex ++;
}
/** Set webpage first actions before user interactions */
function setWebpageDefaults() {
  setMaxNumberOfComments(); // Takes the max number of comments thte user can display
  setRandomImage(); // Displays a random image on the gallery cycler
}

/** Takes the max number of comments available to the user from datastore */
function setMaxNumberOfComments() {
  fetch('/comments-size').then(response => response.text()).then((commentArraySize)  => {
    const maxArraySize = parseInt(commentArraySize);
    const maxNumberofComments = document.getElementById('number-of-comments');

    maxNumberofComments.max = maxArraySize.toString(); // Set the max number as attribute of the input
  });
}

/** Takes the number of comments the user wants to see and displays them */
function loadComments() {
  const numberOfComments = document.getElementById('number-of-comments').value;
  fetch(`/load-comments?number-of-comments=${numberOfComments}`).then(response => response.json()).then((comments) => {
    setMaxNumberOfComments(); // Update the maximum number of comments available.
    appendComments(comments);
  });
}

/** Eliminate displayed comments to load a new set of them */
function removePassedComments(commentWrapper) {
  while(commentWrapper.firstChild) { // While there are comments left, remove them
    commentWrapper.removeChild(commentWrapper.firstChild);
  }
}

/** Display the number selected of comments */
function appendComments(comments) {
  const commentWrapper = document.getElementById('comment-display-container');

  removePassedComments(commentWrapper); // Remove passed comments before loading new ones
  
  for (let index = 0; index < comments.length; index++) {
    const userComment = comments[index];

    /** Defined template of the comment card */
    const userCommentTemplate = document.getElementsByTagName('template')[0];
    const templateClone = userCommentTemplate.content.cloneNode(true);

    /** Add the information to the card */
    templateClone.querySelector('b').textContent = userComment.user;
    templateClone.querySelector('p').textContent = userComment.comment;

    /** If the remove buttons is clicked, remove the comment */
    templateClone.getElementById('close-button-wrapper').addEventListener('click', () => {
      deleteComment(userComment);
    })

    /** Add the comment to the comments container */
    commentWrapper.appendChild(templateClone); 
  }
}

/** Delete comment con event listener */
function deleteComment(userComment) {
  const dataStoreParams = new URLSearchParams();
  dataStoreParams.append('id', userComment.id); // Append the comment datastore id as target.
  fetch('/delete-comment', {method: 'POST', body: dataStoreParams}).then(() => {
    loadComments();
  });
}

/** Suffles the array on every new cycle */
function shuffleGalleryItems() {
  for(let index = GALLERY_ITEMS.length - 1; index > 0; index--) {
    //Looking for random index
    const randomItem = Math.floor(Math.random() * (index + 1));
    // Swaps Elements
    [GALLERY_ITEMS[index], GALLERY_ITEMS[randomItem]] = [GALLERY_ITEMS[randomItem], GALLERY_ITEMS[index]];
  }
}

// When the page starts, display a random image.
window.onload = setWebpageDefaults;