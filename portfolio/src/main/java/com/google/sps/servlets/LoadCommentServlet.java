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
@WebServlet("/load-comments")
public class LoadCommentServlet extends HttpServlet {

  /*private List<UserComments> commentArray = new ArrayList<>();

  private int numberOfComments = 5;

  /** Converts Comment ArrayList to Json 
  public String toJson(HttpServletRequest request) throws IOException {
   return new Gson().toJson();
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

  private List<UserComments> shuffleCommentArray() throws IOException{
    List<UserComments> shuffledArray = commentArray;

    for(int index = shuffledArray.size() - 1; index > 0; index--) {
      int randomIndex = (int)(Math.random() * shuffledArray.size());

      Collections.swap(shuffledArray, index, randomIndex);
    }

    return shuffledArray;
  }

  public List<UserComments> getTrimmedCommentArray(HttpServletRequest request, boolean loadNewComments) throws IOException {
    List<UserComments> trimmedCommentArray = new ArrayList<>();

    if(loadNewComments) {
      numberOfComments += 5;
    }
    
    System.out.println(numberOfComments);
    if(numberOfComments >= commentArray.size()) {
      numberOfComments = 5;
      return commentArray;
    }

    for (int index = 0; index < numberOfComments; index++) {
      UserComments randomUserComment = commentArray.get(index);
      String userComment = randomUserComment.comment;
      String userName = randomUserComment.user;

      randomUserComment = new UserComments(userName,userComment);
      trimmedCommentArray.add(randomUserComment);
    }

    return trimmedCommentArray;
  }
*/
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
   
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
   
  }
}