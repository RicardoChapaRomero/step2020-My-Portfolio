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

/**
* Class to schedule meetings based on attendees free times.
*/

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
    Collection<TimeRange> meetingTimesForOptionalAttendees = new ArrayList<TimeRange>();
    final long meetingDuration = request.getDuration();

    // Return empty list if the meeting last more than a day
    if (TimeRange.WHOLE_DAY.duration() < meetingDuration) {
      return meetingTimesForRequiredAttendees;
    }
    
    // All day available if there are no scheduled events
    if (events.isEmpty()) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    // List of required attendees
    final Collection<String> meetingAttendees = request.getAttendees();

    //Get the times where the attendees are not free
    final PriorityQueue<TimeRange> requiredAttendeesOccupiedTimes = 
      getAttendeesOccupiedTimes(events, meetingAttendees, true);
    final PriorityQueue<TimeRange> optionalAttendeesTimeList = 
      getAttendeesOccupiedTimes(events, meetingAttendees, false);

    /** 
    * Look for time ranges for optional attendees 
    * if required attendees all available all day 
    */
    if (requiredAttendeesOccupiedTimes.isEmpty()) {
      if (optionalAttendeesTimeList.isEmpty()) {
        meetingTimesForRequiredAttendees.add(
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true)
        );

        return meetingTimesForRequiredAttendees;
      } else if (optionalAttendeesTimeList.size() == 1) {

        return getTimeRangesFromSingleEvent(optionalAttendeesTimeList, meetingDuration);
      } else {
         final List<TimeRange> compressedEvents = compressEvents(optionalAttendeesTimeList);
         meetingTimesForOptionalAttendees =  getAvailableTimeRanges(compressedEvents, meetingDuration);

         return meetingTimesForOptionalAttendees;
      }
    }

    else if (requiredAttendeesOccupiedTimes.size() == 1) {
      return 
        getTimeRangesFromSingleEvent(requiredAttendeesOccupiedTimes, meetingDuration);
    }

    /**
    * Convert all overlapping events in to compressed events for faster processing
    */
    final List<TimeRange> compressedEvents = 
      compressEvents(requiredAttendeesOccupiedTimes);
   
    if (compressedEvents.size() == 1) {
      PriorityQueue<TimeRange> compressedEvent = new PriorityQueue();
      compressedEvent.addAll(compressedEvents);
      
      return getTimeRangesFromSingleEvent(compressedEvent, meetingDuration);
    }

    meetingTimesForRequiredAttendees = 
      getAvailableTimeRanges(compressedEvents, meetingDuration);

    Collection<TimeRange> meetingTimesForAllAttendees = 
      meetingTimesForRequiredAttendees;

    /**
    * If the optional attendees have occupied times then 
    * look for overlaps on required attendees times and take
    * those times off available meeting times
    */
    while (optionalAttendeesTimeList.size() != 0) {
      TimeRange optionalTimeRange = optionalAttendeesTimeList.poll();

      if (meetingTimesForRequiredAttendees.contains(optionalTimeRange)) {
        for (Iterator<TimeRange> event = meetingTimesForAllAttendees.iterator(); event.hasNext();) {
          if (event.next().equals(optionalTimeRange)) {
            event.remove();
            break;
          }
        }
      }
    }

    /**
    * If there's no room for optional attendees then 
    * return original time ranges for required attendees
    */
    return (meetingTimesForAllAttendees.isEmpty()) ? 
      meetingTimesForRequiredAttendees : meetingTimesForAllAttendees;
  }

  /**
  * Gets an ordered queue of attendees occupied time ranges
  */
  public PriorityQueue<TimeRange> getAttendeesOccupiedTimes(
    Collection<Event> events, 
    Collection<String> meetingAttendees, 
    boolean lookForRequiredAttendees) {  

      PriorityQueue<TimeRange> attendeesOccupiedTimes = 
        new PriorityQueue<TimeRange>(TimeRange.ORDER_BY_START);

      // Iterate over list of attendees and compare with meeting request attendees
      for (Event singleEvent : events) {
        Set<String> eventAttendees = singleEvent.getAttendees();
        Iterator<String> meetingRequestAttendeeList = meetingAttendees.iterator();
    
        while (meetingRequestAttendeeList.hasNext()) {
          if (eventAttendees.contains(meetingRequestAttendeeList.next()) && lookForRequiredAttendees) { 
            attendeesOccupiedTimes.add(singleEvent.getWhen());
            break;
          }
        }

        if(!lookForRequiredAttendees) {
          attendeesOccupiedTimes.add(singleEvent.getWhen());
        }
      }
      
      return attendeesOccupiedTimes; 
  }

  /**
  * Gets available time ranges, before and after, a single event
  */
  public Collection<TimeRange> getTimeRangesFromSingleEvent(
    PriorityQueue<TimeRange> requiredAttendeesOccupiedTimes,
    long meetingDuration) {

      Collection<TimeRange> meetingTimesForRequiredAttendees = new ArrayList<TimeRange>();
      TimeRange eventTime = requiredAttendeesOccupiedTimes.poll();
      
      if (eventTime.start() - TimeRange.START_OF_DAY >= meetingDuration) {
        TimeRange availableBeforeMeeting = 
          TimeRange.fromStartEnd(TimeRange.START_OF_DAY, eventTime.start(), false);
        meetingTimesForRequiredAttendees.add(availableBeforeMeeting);
      }
      if (TimeRange.END_OF_DAY - eventTime.end() >= meetingDuration) {
        TimeRange availableAfterMeeting = 
          TimeRange.fromStartEnd(eventTime.end(), TimeRange.END_OF_DAY, true);
        meetingTimesForRequiredAttendees.add(availableAfterMeeting);
      }

      return meetingTimesForRequiredAttendees;
  }

  /**
  * Converts any overlapping events into compacted events
  */
  public List<TimeRange> compressEvents(
    PriorityQueue<TimeRange> requiredAttendeesOccupiedTimes) {
      List<TimeRange> compressedEvents = new ArrayList<>();

      TimeRange nextEvent = requiredAttendeesOccupiedTimes.poll();
      while (requiredAttendeesOccupiedTimes.size() != 0) {
        TimeRange followingEvent = requiredAttendeesOccupiedTimes.poll();

        if (nextEvent.overlaps(followingEvent)) {
          // Get the earliest start time of both events
          int startTime = (nextEvent.start() < followingEvent.start()) ? 
            nextEvent.start() : followingEvent.start();
          // Get the latest end time of both events
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

  /**
  * Gets the list of avaiable time ranges for requested meeting
  */
  public Collection<TimeRange> getAvailableTimeRanges(
    List<TimeRange> compressedEvents, long meetingDuration) {
      Collection<TimeRange> meetingTimesForRequiredAttendees = new ArrayList<TimeRange>();

      for (int index = 0; index < compressedEvents.size(); index++) {
      TimeRange singleEvent = compressedEvents.get(index);

        if (index == 0) { // Look for available time range before any event
          if (TimeRange.START_OF_DAY + meetingDuration < singleEvent.start()) {
            TimeRange availableBeforeEvents = 
              TimeRange.fromStartEnd(TimeRange.START_OF_DAY, singleEvent.start(), false);
            meetingTimesForRequiredAttendees.add(availableBeforeEvents);
          }
          continue;
        }

        // Look for available time range between events
        if (singleEvent.start() - compressedEvents.get(index - 1).end() >= meetingDuration) {
            TimeRange availableBetweenEvents = 
              TimeRange.fromStartEnd(compressedEvents.get(index - 1).end(), singleEvent.start(), false);
            meetingTimesForRequiredAttendees.add(availableBetweenEvents);
          } 

        // Look for available time range after all events
        if (index == compressedEvents.size() - 1) {
          if (TimeRange.END_OF_DAY - singleEvent.end() >= meetingDuration) {
            TimeRange availableAfterEvents = 
              TimeRange.fromStartEnd(singleEvent.end(), TimeRange.END_OF_DAY, true);
            meetingTimesForRequiredAttendees.add(availableAfterEvents);
          }
        }
      }

      return meetingTimesForRequiredAttendees;
  }
}
