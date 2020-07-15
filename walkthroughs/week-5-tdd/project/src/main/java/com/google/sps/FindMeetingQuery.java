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

package com.google.sps;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>(); //Arrays.asList();
    System.out.println(request.getDuration());

    if(TimeRange.WHOLE_DAY.duration() < request.getDuration()) {
      return possibleMeetingTimes;
    }
    
    if(events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collection<String> meetingAttendees = request.getAttendees();
    List<TimeRange> eventTimes = new ArrayList<TimeRange>();

    for(Iterator<Event> iterator = events.iterator(); iterator.hasNext();) {
      Event event = iterator.next();
      Set<String> eventAttendees = event.getAttendees();

      Iterator<String> meetingAttendeesList = meetingAttendees.iterator();

      while(meetingAttendeesList.hasNext()) {
        if (eventAttendees.contains(meetingAttendeesList.next())) {
          eventTimes.add(event.getWhen());
          break;
        }
      }
    }

    if (eventTimes.size() == 1) {
      TimeRange eventTime = eventTimes.get(0);
      if(eventTime.start() > TimeRange.START_OF_DAY && ((eventTime.start() - TimeRange.START_OF_DAY) >= request.getDuration())) {
        TimeRange availableBeforeMeeting = TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventTime.start(), false);
        possibleMeetingTimes.add(availableBeforeMeeting);
      }
      if(eventTime.end() < TimeRange.END_OF_DAY && ((TimeRange.END_OF_DAY - eventTime.end()) >= request.getDuration())) {
        TimeRange availableAfterMeeting = TimeRange.fromStartEnd(eventTime.end(), TimeRange.END_OF_DAY, true);
        possibleMeetingTimes.add(availableAfterMeeting);
      }
      return possibleMeetingTimes;
    }


    return possibleMeetingTimes;
    
    //throw new UnsupportedOperationException("TODO: Implement this method.");
  }
}
