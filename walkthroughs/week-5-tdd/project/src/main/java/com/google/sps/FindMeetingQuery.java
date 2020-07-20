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
    Collection<TimeRange> meetingTimesForRequiredAttendees = new ArrayList<TimeRange>();
    final long meetingDuration = request.getDuration();

    if (TimeRange.WHOLE_DAY.duration() < meetingDuration) {
      return meetingTimesForRequiredAttendees;
    }
    
    if (events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    final Collection<String> meetingAttendees = request.getAttendees();
    final PriorityQueue<TimeRange> requiredAttendeesOccupiedTimes = 
      getAttendeesOccupiedTimes(events, meetingAttendees, true);
    final PriorityQueue<TimeRange> optionalAttendeesTimeList = 
      getAttendeesOccupiedTimes(events, meetingAttendees, false);


    if (requiredAttendeesOccupiedTimes.isEmpty()) {
      if (optionalAttendeesTimeList.isEmpty()) {
        meetingTimesForRequiredAttendees.add(
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true)
        );
        return meetingTimesForRequiredAttendees;
      } else if(optionalAttendeesTimeList.size() == 1){
        return getTimeRangesFromSingleEvent(optionalAttendeesTimeList, meetingDuration);
      }
    }

    else if (requiredAttendeesOccupiedTimes.size() == 1) {
      return getTimeRangesFromSingleEvent(requiredAttendeesOccupiedTimes, meetingDuration);
    }

    final List<TimeRange> compressedEvents = compressEvents(requiredAttendeesOccupiedTimes);
   
    if (compressedEvents.size() == 1) {
      PriorityQueue<TimeRange> compressedEvent = new PriorityQueue();
      compressedEvent.addAll(compressedEvents);
      
      return getTimeRangesFromSingleEvent(compressedEvent, meetingDuration);
    }

    meetingTimesForRequiredAttendees = getAvailableTimeRanges(compressedEvents, meetingDuration);

    Collection<TimeRange> meetingTimesForAll = meetingTimesForRequiredAttendees;

    if(optionalAttendeesTimeList.size() != 0) {
      while(optionalAttendeesTimeList.size() != 0) {
        TimeRange optionalTimeRange = optionalAttendeesTimeList.poll();

        if(meetingTimesForRequiredAttendees.contains(optionalTimeRange)) {
          for(Iterator<TimeRange> event = meetingTimesForAll.iterator(); event.hasNext();) {
            if (event.next().equals(optionalTimeRange)) {
              event.remove();
              break;
            }
          }
        }
      }
    }

    return (meetingTimesForAll.isEmpty()) ? meetingTimesForRequiredAttendees : meetingTimesForAll;
    

  }

  public PriorityQueue<TimeRange> getAttendeesOccupiedTimes(
      Collection<Event> events, 
      Collection<String> meetingAttendees, 
      boolean lookForRequiredAttendees
    ) {
  
    PriorityQueue<TimeRange> attendeesOccupiedTimes = 
      new PriorityQueue<TimeRange>(TimeRange.ORDER_BY_START);

    for (Event singleEvent : events) {
      Set<String> eventAttendees = singleEvent.getAttendees();
      Iterator<String> meetingRequestAttendeeList = meetingAttendees.iterator();
      boolean hasRequestedAttendee = false;

      while (meetingRequestAttendeeList.hasNext()) {
        if (eventAttendees.contains(meetingRequestAttendeeList.next()) && lookForRequiredAttendees) { 
          attendeesOccupiedTimes.add(singleEvent.getWhen());
          hasRequestedAttendee = true;
          break;
        }
      }

      if(!hasRequestedAttendee && !lookForRequiredAttendees) {
        attendeesOccupiedTimes.add(singleEvent.getWhen());
      }
    }
    
    return attendeesOccupiedTimes; 
  }

  public Collection<TimeRange> getTimeRangesFromSingleEvent(
    PriorityQueue<TimeRange> requiredAttendeesOccupiedTimes,
    long meetingDuration) {
      Collection<TimeRange> meetingTimesForRequiredAttendees = new ArrayList<TimeRange>();
      TimeRange eventTime = requiredAttendeesOccupiedTimes.poll();
      
      if(eventTime.start() - TimeRange.START_OF_DAY >= meetingDuration) {
        TimeRange availableBeforeMeeting = 
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventTime.start(), false);
        meetingTimesForRequiredAttendees.add(availableBeforeMeeting);
      }
      if(TimeRange.END_OF_DAY - eventTime.end() >= meetingDuration) {
        TimeRange availableAfterMeeting = 
          TimeRange.fromStartEnd(eventTime.end(), TimeRange.END_OF_DAY, true);
        meetingTimesForRequiredAttendees.add(availableAfterMeeting);
      }

      return meetingTimesForRequiredAttendees;
  }

  public List<TimeRange> compressEvents(
    PriorityQueue<TimeRange> requiredAttendeesOccupiedTimes) {
      List<TimeRange> compressedEvents = new ArrayList<>();

      TimeRange nextEvent = requiredAttendeesOccupiedTimes.poll();
      while (requiredAttendeesOccupiedTimes.size() != 0) {
        TimeRange followingEvent = requiredAttendeesOccupiedTimes.poll();

        if (nextEvent.overlaps(followingEvent)) {
          int startTime = (nextEvent.start() < followingEvent.start()) ? 
            nextEvent.start() : followingEvent.start();
          int endTime = (nextEvent.end() > followingEvent.end()) ? 
            nextEvent.end() : followingEvent.end();

          compressedEvents.add(
            TimeRange.fromStartDuration(startTime, endTime - startTime)
          );

          nextEvent = followingEvent;
          
        } else {
          compressedEvents.add(nextEvent);
          compressedEvents.add(followingEvent);
        }
      }
      return compressedEvents;
  }

  public Collection<TimeRange> getAvailableTimeRanges(
    List<TimeRange> compressedEvents, long meetingDuration) {
      Collection<TimeRange> meetingTimesForRequiredAttendees = new ArrayList<TimeRange>();

      for (int index = 0; index < compressedEvents.size(); index++) {
      TimeRange singleEvent = compressedEvents.get(index);

        if (index == 0) {
          if (TimeRange.START_OF_DAY + meetingDuration < singleEvent.start()) {
            TimeRange availableBeforeMeeting = 
              TimeRange.fromStartEnd(TimeRange.START_OF_DAY, singleEvent.start(), false);
            meetingTimesForRequiredAttendees.add(availableBeforeMeeting);
          }
          continue;
        }

        if (singleEvent.start() - compressedEvents.get(index - 1).end() >= meetingDuration) {
            TimeRange availableBetweenMeetings = 
              TimeRange.fromStartEnd(compressedEvents.get(index - 1).end(), singleEvent.start(), false);
            meetingTimesForRequiredAttendees.add(availableBetweenMeetings);
          } 

        if (index == compressedEvents.size() - 1) {
          if (TimeRange.END_OF_DAY - singleEvent.end() >= meetingDuration) {
            TimeRange availableAfterMeeting = 
              TimeRange.fromStartEnd(singleEvent.end(), TimeRange.END_OF_DAY, true);
            meetingTimesForRequiredAttendees.add(availableAfterMeeting);
          }
        }
      }

      return meetingTimesForRequiredAttendees;
  }
}
