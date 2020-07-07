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
 * Handles the Auth API requests
 */

// Variable to keep track of the auth status in the current session
let verificationStatus = false; /** @type { bool } */

// URL to redirect on any log in/out request
let auth_url = ""; /** @type { string } */

/** Change the text of the button  */
function setAuthButtonText() {
  const authButton = document.getElementById('auth_button');

  authButton.innerText = (verificationStatus) ? 
    authButton.innerText = 'Log Out' : authButton.innerText = 'Log In';
}

/** Get the Auth status on every refresh */
function verifyAuth() {
  fetch('/auth').then(response => response.json()).then((authStatus) => {

    verificationStatus = authStatus.isVerified;
    auth_url = authStatus.url

    setAuthButtonText(); // Set the status of the button on every refresh
  });
}

/** Refresh the page making the Auth API request */
function handleAuthRequest() {
  window.location.href =  window.location.href + auth_url;
}