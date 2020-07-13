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

package com.google.sps.servlets;

import com.google.gson.Gson;
import com.google.sps.usercomment.UserComments;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that gets a new comment from the portfolio */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  public static final String MAIN_PAGE_URL = "/";

  // Get the comment sentiment using Google's Sentiment Analysis API
  public float getCommentSentiment(String comment) throws IOException{
    Document sentimentDoc =
      Document.newBuilder().setContent(comment).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(sentimentDoc).getDocumentSentiment();
    float sentimentScore = sentiment.getScore();

    languageService.close();

    return sentimentScore;
  }
  
  /** Write a new message to DataStore */
  private void toDatastore(String comment, String username, UserService userService) throws IOException {
    // Create a new entity to save in datastore 
    Entity newComment = new Entity("Comment");

    // Set the entity's values { key: value } 
    newComment.setProperty("comment", comment); 
    newComment.setProperty("user", username);
    newComment.setProperty("sentiment-score", getCommentSentiment(comment));
    newComment.setProperty("email", userService.getCurrentUser().getEmail());
    newComment.setProperty("userId", userService.getCurrentUser().getUserId());

    // Call to get datastore service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newComment); // Write the entity into datastore 
  }

  /** Redirection to the main page */
  private void doRedirect(HttpServletResponse response) throws IOException {
    response.sendRedirect(MAIN_PAGE_URL);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      doRedirect(response);
      return;
    }
    
    // Get the user inputs 
    String comment = request.getParameter("user-comment");
    String username = request.getParameter("user-name");

    // Redirection if comment is empty or accidental click
    if (comment == null || comment.length() == 0) {
      doRedirect(response);
      return;
    }

    // Anonymous for every comment that doesn't come with a name
    if (username == null || username.length() == 0) {
      username = "Anonymous";
    }

    toDatastore(comment,username,userService); // Add to datastore
    doRedirect(response);
  }
}
