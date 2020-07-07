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

package com.google.sps.verifyusercommentid;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet responsible to remove every comment requested by the user */
@WebServlet("/verify-user-comment-id")
public class VerifyUserCommentID extends HttpServlet {

  public boolean verifyCommentID(String commentEmail) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    String userEmail = userService.getCurrentUser().getEmail();

    return (commentEmail.equals(userEmail));
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentEmail = request.getParameter("comment-email"); // Get the comment Id

    response.setContentType("text/html");
    response.getWriter().println(verifyCommentID(commentEmail));
  }
}