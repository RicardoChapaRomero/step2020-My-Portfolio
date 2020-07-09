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

package com.google.sps.usercomment;

/**
* Class object to define the structure of a user comment.
*/
public class UserComments {
  private String user;
  private String comment;
  private String email;
  private String userId;
  private long id;

  /** Constructor(String, String, long)*/
  public UserComments(String userInput, String commentInput, String userEmail, String userID, long entityID) {
    user = userInput;
    comment = commentInput;
    email = userEmail;
    userId = userID;
    id = entityID;
  }

  public String getUser() {
    return this.user;
  }

  public String getComment() {
    return this.comment;
  }

  public String getEmail() {
    return this.email;
  }

  public String getUserID() {
    return this.userId;
  }

  public long getID() {
    return this.id;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setUserID(String userId) {
    this.userId = userId;
  }

  public void setID(long id) {
    this.id = id;
  }
}
