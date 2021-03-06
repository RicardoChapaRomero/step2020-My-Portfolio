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
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/load-comments")
public class LoadCommentServlet extends HttpServlet {

  /** @private {!Array<{String user, String comment, String email, String userID,float commentSentiment, long id}>} */
  private List<UserComments> commentArray = new ArrayList<>();
  private int numberOfComments; // Number of displayed comments selected by the user
  private String languageCode; // Language abreviation following ISO 639 standard
  public static final String REDIRECT_URL = "/"; // Redirect to Portfolio

  // Get the comment translation using Google's Translate API
  public String translateComment(String comment) throws IOException {
    Translate translateService = TranslateOptions.getDefaultInstance().getService();
     // Do the comment translation.
    Translation commentTranslation =
        translateService.translate(comment, Translate.TranslateOption.targetLanguage(languageCode));
    String translatedText = commentTranslation.getTranslatedText();

    return translatedText;
  }

  /** Gets the translated comment in the requested language code */
  public String getComment(String comment, Entity commentEntity, DatastoreService datastore) throws IOException {
    EmbeddedEntity listOfTranslatedComments = 
      (EmbeddedEntity) commentEntity.getProperty("comments");
    String translatedCommentInLanguageCode = 
      (String) listOfTranslatedComments.getProperty(languageCode);

    // Translate the comment if the translation doesn't exist
    if (translatedCommentInLanguageCode == null) {
      translatedCommentInLanguageCode = translateComment(comment);
     
      listOfTranslatedComments.setProperty(languageCode,translatedCommentInLanguageCode);
      commentEntity.setProperty("comments", listOfTranslatedComments);
      datastore.put(commentEntity);
    }
    return translatedCommentInLanguageCode;
  }

  public void loadComments() throws IOException {
    Query commentsQuery = new Query("Comment"); // Get previous stored comments
    commentArray.clear(); // Empty the array on every comments GET.

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService(); // Get datastore service
    
    // Prepare to instance the stored comments
    Iterable<Entity> comments = 
      datastore.prepare(commentsQuery).asIterable(FetchOptions.Builder.withLimit(numberOfComments));

    for (Entity commentEntity : comments) {
      // Get the values of every stored comment
      long id = commentEntity.getKey().getId();
      String comment = (String) commentEntity.getProperty("comment");

      String translatedComment = getComment(comment,commentEntity,datastore);

      double sentimentScore = (double)commentEntity.getProperty("sentiment-score");
      String user = (String) commentEntity.getProperty("user");
      String email = (String) commentEntity.getProperty("email");
      String userId = (String) commentEntity.getProperty("userId");

      UserComments userCommentEntity = 
        new UserComments(user,translatedComment,email,userId,sentimentScore,id); 
      commentArray.add(userCommentEntity); // Add the value to the comments array
    }
  }

  /** Translate Array List to JSON */
  public String commentsToJson() throws IOException {
    return new Gson().toJson(commentArray);
  }

  /** Get the number of comments displayed selected by the user */
  public int getNumberOfComments(HttpServletRequest request) throws IOException {
    String value = request.getParameter("number-of-comments");  // Get user input
    int selectedNumberOfComments;

    // If the selector is empty then return 5 as default.
    selectedNumberOfComments = (value == null || value.length() == 0) ? 5 : Integer.parseInt(value);

    return selectedNumberOfComments;
  }

  // Get the language into which the comments will be translated
  public String getLanguageCode(HttpServletRequest request) throws IOException {
    String languageCode = request.getParameter("language_code");

    return languageCode;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      response.sendRedirect(REDIRECT_URL);
      return;
    }
    
    numberOfComments = getNumberOfComments(request);
    languageCode = getLanguageCode(request);
    loadComments();
  
    /** Send Get response to the wepage */
    response.setContentType("application/json; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().println(commentsToJson());
  }
}
