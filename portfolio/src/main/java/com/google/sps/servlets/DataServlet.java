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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class DataServlet extends HttpServlet {

  private class UserComments {
    public String user;
    public String comment;

    public UserComments(String userInput, String commentInput) {
      user = userInput;
      comment = commentInput;
    }
  }

  private List<UserComments> commentArray = new ArrayList<>();
  private int numberOfComments = 1;

  private int getNumberOfComments(HttpServletRequest request) throws IOException {

    /** TODO: Trim the array in parts*/
    return commentArray.size();
  }

  private List<UserComments> shuffleCommentArray() throws IOException{
    List<UserComments> shuffledArray = commentArray;

    for(int index = shuffledArray.size() - 1; index > 0; index--) {
      int randomIndex = (int)(Math.random() * shuffledArray.size());

      Collections.swap(shuffledArray, index, randomIndex);
    }

    return shuffledArray;
  }

  public List<UserComments> getTrimmedCommentArray(HttpServletRequest request) throws IOException {

    int numberOfComments = getNumberOfComments(request);
    List<UserComments> shuffledCommentArray = shuffleCommentArray();
    List<UserComments> trimmedCommentArray = new ArrayList<>();

    if(numberOfComments >= shuffledCommentArray.size()) {
      return shuffledCommentArray;
    }

    for (int index = 0; index < numberOfComments; index++) {
      UserComments randomUserComment = shuffledCommentArray.get(index);
      String userComment = randomUserComment.comment;
      String userName = randomUserComment.user;

      randomUserComment = new UserComments(userName,userComment);
      trimmedCommentArray.add(randomUserComment);
    }
    return trimmedCommentArray;
  }

  /** Redirection to the main page */
  private void doRedirect(HttpServletResponse response) throws IOException {
    response.sendRedirect("/index.html");
  }

  /** Write a new message to DataStore */
  private void toDatastore(String comment, String username) throws IOException {
    // Create a new entity to save in datastore 
    Entity newComment = new Entity("Comment");

    // Set the entity's values { key: value } 
    newComment.setProperty("comment", comment); 
    newComment.setProperty("user",username);

    // Call to get datastore service
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(newComment); // Write the entity into datastore 
  }

  private void dataServletResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html;");
    response.getWriter().println(toJson(request));
  }

  public void loadComments() throws IOException {
    Query commentsQuery = new Query("Comment"); // Get previous stored comments
    commentArray = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    // Prepare to instance the past stored comments
    PreparedQuery comments = datastore.prepare(commentsQuery);

    for (Entity commentEntity : comments.asIterable()) {
      // Get the value of every stored comment
      String comment = (String) commentEntity.getProperty("comment");
      String user = (String ) commentEntity.getProperty("user");

      UserComments userCommentEntity = new UserComments(user,comment); 
      commentArray.add(userCommentEntity); // Add the value to the comments array
    }
  }

  /** Converts Comment ArrayList to Json */
  public String toJson(HttpServletRequest request) throws IOException {
   return new Gson().toJson(getTrimmedCommentArray(request));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String value = request.getParameter("user-comment");
    String username = request.getParameter("user-name");

    /** Redirection if value is empty or accidental click */
    if(value == null || value.length() == 0) {
      doRedirect(response);
      dataServletResponse(request, response);
      return;
    }

    if(username == null || username.length() == 0) {
      username = "";
    }

    UserComments newUserComment = new UserComments(value, username);
    commentArray.add(newUserComment); // Add every new submition to recorded comments 
    toDatastore(value,username);

    dataServletResponse(request, response);
    doRedirect(response);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    /** Load previous stored comments */
    loadComments();
    dataServletResponse(request, response);
  }
}
