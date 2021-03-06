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
 * Calls to Datastore servlet to load and add comments
 */

 const SENTIMENT_PROPERTIES = {
    happyProperties: {text: 'Happy', color:'rgba(139, 195, 74, 1)'},
    neutralProperties: {text: 'Neutral', color:'rgba(189, 189, 189, 1)'},
    angryProperties: {text: 'Angry', color:'rgba(244, 67, 54, 1)'}
 };


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
  const commentWrapper = document.getElementById('comment-display-container');
  const numberOfComments = document.getElementById('number-of-comments').value;
  const languageCode = document.getElementById('language_selector').value;

  commentWrapper.innerHTML = 'Loading Comments...'; // Loading Comment

  fetch(`/load-comments?number-of-comments=${numberOfComments}&language_code=${languageCode}`)
    .then(response => {
      if (response.redirected) {
        alert('Login to load the comments');
        return;
      }
      response.json().then((comments) => {
        setMaxNumberOfComments(); // Update the maximum number of comments available.
        removeCommentsFromDOM(); // Remove passed comments before loading new ones
        addCommentsToDOM(comments);
      });
  });
}

/** Eliminate displayed comments to load a new set of them */
function removeCommentsFromDOM() {
  const commentWrapper = document.getElementById('comment-display-container');

  while (commentWrapper.firstChild) { 
    // While there are comments left, remove them
    commentWrapper.removeChild(commentWrapper.firstChild);
  }
}

/** @param {!Array<{user: string, comment: string, email: string, userID: string,commentSentiment:number, id: number}>} */
function addCommentsToDOM(comments) {
  const commentWrapper = document.getElementById('comment-display-container');
  
  for (let index = 0; index < comments.length; index++) {
    const userComment = comments[index];

    /** Defined template of the comment card */
    const userCommentTemplate = document.getElementsByTagName('template')[0];
    const templateClone = userCommentTemplate.content.cloneNode(true);
    const commentHeaderColor = templateClone.getElementById('user-comment-header').style;
    const sentimentContainer = templateClone.getElementById('sentiment');

    /** Add the information to the card */
    templateClone.querySelector('b').textContent = userComment.user;
    templateClone.querySelector('p').textContent = userComment.comment;

    setHeaderProperties(commentHeaderColor,sentimentContainer,userComment.sentimentScore);

    /** If the remove buttons is clicked, remove the comment */
    templateClone.getElementById('close-button-wrapper').addEventListener('click', () => {
      handleDeleteCommentRequest(userComment);
    });

    /** Add the comment to the comments container */
    commentWrapper.appendChild(templateClone); 
  }
}

// Set the header color and sentiment description based on the sentiment of the comment
/** @param {commentHeaderColor:DOMObject, sentimentContainer:DOMObject, commentSentiment: number} */
function setHeaderProperties(commentHeaderColor,sentimentContainer,commentSentimentScore) {
  if (commentSentimentScore >= 0.5) {
    commentHeaderColor.backgroundColor = SENTIMENT_PROPERTIES.happyProperties.color;
    sentimentContainer.textContent = SENTIMENT_PROPERTIES.happyProperties.text;
    return;
  }
  else if (commentSentimentScore >= -.5) {
    commentHeaderColor.backgroundColor = SENTIMENT_PROPERTIES.neutralProperties.color;
    sentimentContainer.textContent = SENTIMENT_PROPERTIES.neutralProperties.text;
    return;
  }

  commentHeaderColor.backgroundColor = SENTIMENT_PROPERTIES.angryProperties.color;
  sentimentContainer.textContent = SENTIMENT_PROPERTIES.angryProperties.text;
}

/** @param {user: string, comment: string, email: string, userID: string, id: number} */
function handleDeleteCommentRequest(userComment) {
  fetch(`/handle-delete-comment?comment-userID=${userComment.userId}`)
    .then(response => response.text()).then((commentIsFromUser) => {

        commentIsFromUser = commentIsFromUser.toString();
        const matchingID = commentIsFromUser.includes('true');

        if (matchingID) {
          deleteComment(userComment);
        } else {
          alert('You can\'t delete other\'s comments');
        }
    });	 
}

/** @param {{user: string, comment: string, email: string, userID: string, id: number}} */
function deleteComment(userComment) {
  const dataStoreParams = new URLSearchParams();
  dataStoreParams.append('id', userComment.id); // Append the comment datastore id as target.
  fetch('/delete-comment', {method: 'POST', body: dataStoreParams}).then(() => {
    loadComments();
  });
}
