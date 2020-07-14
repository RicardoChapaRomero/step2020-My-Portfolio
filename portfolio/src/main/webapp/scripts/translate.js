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
 * Handles the Translate API requests
 */

/** Adds every language supported by Google's Translation API */
function getSupportedLanguages() {
  fetch('/supported-languages').then(response => response.json()).then((supportedLanguages) => {
    const languageSelector = document.getElementById('language_selector');

    supportedLanguages.forEach((language) => {
      const languageOption = document.createElement('option');

      languageOption.text = language.name;
      languageOption.value = language.code;

      languageSelector.appendChild(languageOption);
    });
  });
}
