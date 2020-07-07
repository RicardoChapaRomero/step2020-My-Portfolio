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

package com.google.sps.libraries.authstatus;

/**
* Class object to define the structure of a user comment.
*/
public class AuthStatus {
  private String url;
  private boolean isVerified;
  

  /** Constructor()*/
  public AuthStatus() {
    this.url = "";
    this.isVerified = false;
  }
  
  /** Constructor(String, String, long)*/
  public AuthStatus(String url, boolean isVerified) {
    this.url = url;
    this.isVerified = isVerified;
  }

  public String getUrl() {
    return this.url;
  }

  public boolean getVerificationStatus() {
    return this.isVerified;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setVerificationStatus(boolean isVerified) {
    this. isVerified = isVerified;
  }
}
