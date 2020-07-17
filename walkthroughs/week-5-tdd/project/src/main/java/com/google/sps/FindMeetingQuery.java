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
import java.util.PriorityQueue;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();
    Collection<TimeRange> possibleMeetingTimesForOptinalAttendeees = new ArrayList<TimeRange>();

    final long meetingDuration = request.getDuration();

    if (TimeRange.WHOLE_DAY.duration() < meetingDuration) {
      return possibleMeetingTimes;
    }
    
    if (events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    final Collection<String> meetingAttendees = request.getAttendees();
    final PriorityQueue<TimeRange> eventTimesList = 
      new PriorityQueue<TimeRange>(TimeRange.ORDER_BY_START);
    final PriorityQueue<TimeRange> optionalAttendeesTimeList = 
      new PriorityQueue<TimeRange>(TimeRange.ORDER_BY_START);
    final List<TimeRange> compressedEvents = new ArrayList<>();

    for (Event singleEvent : events) {
      boolean hasRequiredAttendees = false;
      Set<String> eventAttendees = singleEvent.getAttendees();
      Iterator<String> meetingAttendeesList = meetingAttendees.iterator();

      while (meetingAttendeesList.hasNext()) {
        if (eventAttendees.contains(meetingAttendeesList.next())) { 
          eventTimesList.add(singleEvent.getWhen());
          hasRequiredAttendees = true;
          break;
        }
      }

      if(!hasRequiredAttendees) {
        optionalAttendeesTimeList.add(singleEvent.getWhen());
      }
    }

    if (eventTimesList.size() == 0) {
      possibleMeetingTimes.add(
        TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true)
      );
      return possibleMeetingTimes;
    }

    else if (eventTimesList.size() == 1) {
      return getTimeRangesFromSingleEvent(eventTimesList, meetingDuration);
    }

    TimeRange nextEvent = eventTimesList.poll();
    while (eventTimesList.size() != 0) {
      TimeRange followingEvent = eventTimesList.poll();

      if (nextEvent.overlaps(followingEvent)) {
        int startTime = (nextEvent.start() < followingEvent.start()) ? 
          nextEvent.start() : followingEvent.start();
        int endTime = (nextEvent.end() > followingEvent.end()) ? 
          nextEvent.end() : followingEvent.end();

        compressedEvents.add(
          TimeRange.fromStartDuration(startTime, endTime - startTime)
        );

        nextEvent = followingEvent;
        continue;
      }
      compressedEvents.add(nextEvent);
      compressedEvents.add(followingEvent);
    }
   
    if (compressedEvents.size() == 1) {
      if (compressedEvents.get(0).start() - TimeRange.START_OF_DAY >= meetingDuration) {
        TimeRange availableBeforeMeeting = 
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, compressedEvents.get(0).start(), false);
        possibleMeetingTimes.add(availableBeforeMeeting);
      }
      if (TimeRange.END_OF_DAY - compressedEvents.get(0).end() >= meetingDuration) {
        TimeRange availableAfterMeeting = 
          TimeRange.fromStartEnd(compressedEvents.get(0).end(), TimeRange.END_OF_DAY, true);
        possibleMeetingTimes.add(availableAfterMeeting);
      }
      return possibleMeetingTimes;
    }

    for (int index = 0; index < compressedEvents.size(); index++) {
      TimeRange singleEvent = compressedEvents.get(index);

      if (index == 0) {
        if (TimeRange.START_OF_DAY + meetingDuration < singleEvent.start()) {
          TimeRange availableBeforeMeeting = 
            TimeRange.fromStartEnd(TimeRange.START_OF_DAY, singleEvent.start(), false);
          possibleMeetingTimes.add(availableBeforeMeeting);
        }
        continue;
      }

      if (singleEvent.start() - compressedEvents.get(index - 1).end() >= meetingDuration) {
         TimeRange availableBetweenMeetings = 
            TimeRange.fromStartEnd(compressedEvents.get(index - 1).end(), singleEvent.start(), false);
          possibleMeetingTimes.add(availableBetweenMeetings);
       } 

      if (index == compressedEvents.size() - 1) {
       if (TimeRange.END_OF_DAY - singleEvent.end() >= meetingDuration) {
         TimeRange availableAfterMeeting = 
            TimeRange.fromStartEnd(singleEvent.end(), TimeRange.END_OF_DAY, true);
          possibleMeetingTimes.add(availableAfterMeeting);
       }
      }
    }

    Collection<TimeRange> meetingTimesForAll = possibleMeetingTimes;

    System.out.println(optionalAttendeesTimeList);

    if(optionalAttendeesTimeList.size() != 0) {
      while(optionalAttendeesTimeList.size() != 0) {
        TimeRange optionalTimeRange = optionalAttendeesTimeList.poll();

        if(possibleMeetingTimes.contains(optionalTimeRange)) {
          for(Iterator<TimeRange> event = meetingTimesForAll.iterator(); event.hasNext();) {
            if (event.next().equals(optionalTimeRange)) {
              event.remove();
              break;
            }
          }
        }
      }
    }

    System.out.println(possibleMeetingTimes);
    System.out.println(meetingTimesForAll);


    return (meetingTimesForAll.isEmpty()) ? possibleMeetingTimes : meetingTimesForAll;
    
    //throw new UnsupportedOperationException("TODO: Implement this method.");
  }

  private Collection<TimeRange> getTimeRangesFromSingleEvent(
    PriorityQueue<TimeRange> eventTimesList,
    long meetingDuration
    ) {

      Collection<TimeRange> possibleMeetingTimes = new ArrayList<TimeRange>();
      TimeRange eventTime = eventTimesList.poll();
      
      if(eventTime.start() - TimeRange.START_OF_DAY >= meetingDuration) {
        TimeRange availableBeforeMeeting = 
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventTime.start(), false);
        possibleMeetingTimes.add(availableBeforeMeeting);
      }
      if(TimeRange.END_OF_DAY - eventTime.end() >= meetingDuration) {
        TimeRange availableAfterMeeting = 
          TimeRange.fromStartEnd(eventTime.end(), TimeRange.END_OF_DAY, true);
        possibleMeetingTimes.add(availableAfterMeeting);
      }

      return possibleMeetingTimes;
  }
}
