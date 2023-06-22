import { Component, OnInit, ViewChild } from '@angular/core';
import { Calendar, CalendarOptions, DateSelectArg, EventApi, EventClickArg, EventContentArg } from '@fullcalendar/core';
import { Backend2Service } from '../services/backend-2.service';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { Event, EventData, EventGroupAttd, EventResult } from '../models';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {

  events: Event[] = [];
  calEvents: any[] = []
  selectedDate!: Date
  title!: string
  startTime!: string
  endTime!: string
  eventOptions: string[] = ['Training', 'Friendly', 'Competition']
  eventAdded: boolean = false; 
  eventDeleted: boolean = false; 
  notifEvent!: string

  eventGrpAttd: EventGroupAttd[] = []
  eventYes: string[] = []
  eventNo: string[] = []
  eventResults: EventResult[] = []

  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    selectable: true,
    select: this.handleDateSelect.bind(this),
    plugins: [interactionPlugin, dayGridPlugin],
    eventDisplay: 'block',
    eventBackgroundColor: 'darkblue',
    eventClick: this.handleEventClick.bind(this)
  }

  @ViewChild('calendar') calendar: any; 

  constructor(private backend2Svc: Backend2Service) {}

  ngOnInit(): void {
    // get events 
    this.backend2Svc.getCalEvents().then((result:any) => {
      if (result.message == 'empty') {
        console.info('>>> Message: ', result.message);
      } else {
        console.info('>>> Event in DB: ', result)
        this.events = result; 
        // sort by date 
        this.events.sort(this.sortByDate)
        // convert events to suitable reading by calendarOptions
        this.events.forEach(event => {
          // convert time (8:30) to 2023-06-15T08:30:00
          const eventInput = {
            id: event.eventId,
            title: event.title,
            start: this.convertDateTime(event.startTime, event.selectedDate), 
            end: this.convertDateTime(event.endTime, event.selectedDate),
            allDay: false
          }
          this.calEvents.push(eventInput)
        })
        console.info('>>> Calendar Events created: ', this.calEvents);
        this.calendarOptions.events = this.calEvents; // on change in calendarOptions, refresh the page?
      }
    })

    // get previously saved attendance
    const username = localStorage.getItem('username')!
    this.backend2Svc.getIndvAttendance(username).then((result:any) => {
      if (result.message == 'empty') {
        console.info('>>> Message: ', result.message)
      } else {
        this.eventResults = result
      }
    })
  }

  attendance(eventId:string) {
    // get attendance of eventId
    this.backend2Svc.getGroupAttendance(eventId).then((result:any) => {
      if (result.message == 'empty') {
        console.info('>>> Message: ', result.message)
      } else {
        this.eventGrpAttd = result;
        // go thru the list to see who wrote 'yes' and 'no'
        this.eventGrpAttd.forEach((user) => {
          if (user.response === 'yes') {
            this.eventYes.push(user.username); 
          } else { // will be no
            this.eventNo.push(user.username);
          }
        })
        console.info('>>> grp attendance (yes): ', this.eventYes)
        console.info('>>> grp attendance (no): ', this.eventNo)
      }
    })
  }

  choice(response:string, eventId: string) {
    // check existing array 
    const existingResult = this.eventResults.find((result) => result.eventId === eventId )
    if (existingResult)
      existingResult.response = response; 
    else 
      this.eventResults.push({eventId, response})
  }

  getButtonColor(value:string, eventId: string) {
    const eventResult = this.eventResults.find((result) => result.eventId === eventId)
    return eventResult && eventResult.response === value ? 'primary' : '';
  }

  save() {
    // update database and overwrite instead of resave
    const username = localStorage.getItem('username')!
    const eventData: EventData = {results: this.eventResults, username: username}
    this.backend2Svc.saveAttendance(eventData).then((result) => {
      console.info('>>> Save attendance: ', result);
    })
  }

  addEvent() {
    const uuid = uuidv4().slice(0,5);
    const event: Event = {'eventId': uuid, 'selectedDate': this.selectedDate, 'title': this.title, 'startTime': this.startTime, 'endTime': this.endTime}
    
    // add to list of events 
    this.events.push(event); 
    const eventInput = {
      id: event.eventId,
      title: event.title,
      start: this.convertDateTime(event.startTime, event.selectedDate),
      end: this.convertDateTime(event.endTime, event.selectedDate),
      allDay: false 
    }
    this.calEvents.push(eventInput);
    // this.calendarOptions = { ... this.calendarOptions, events: this.calEvents}
    console.info('>>> New calendar options: ', this.calendarOptions)
    console.info('>>> New calendar events: ', this.calEvents)

    // TODO: How to auto re-render page 
    this.calendar.getApi().addEvent(eventInput);

    // send to backend to save in list of events 
    this.backend2Svc.storeEventToDB(event).then((result:any) => {
      console.info('>>> Store event to DB: ', result.isAdded);
    })

    // Direct to send notification page 
    this.eventAdded = true; 
    this.backend2Svc.notifType = 'add'; 
    this.backend2Svc.notifEvent = this.title;
  }
  
  // cancel event 
  handleEventClick(clickInfo: EventClickArg) {
    if (confirm('Are you sure you want to delete this event?')) {
      // remove event from calendar
      clickInfo.event.remove();

      // update database 
      const eventId = clickInfo.event.id 
      this.backend2Svc.removeEventFromDB(eventId).then((result:any) => {
        console.info('>>> Remove event from DB: ', result.isRemoved)
      })

      // send to notification page
      this.eventDeleted = true; 
      this.backend2Svc.notifType = 'cancel'
      this.notifEvent = this.backend2Svc.notifEvent = clickInfo.event.title
    }
  }

  handleDateSelect(selectInfo: DateSelectArg) {
    this.selectedDate = selectInfo.start
  }

  convertDateTime(time:string, selectedDate:Date) {
    const [hours, minutes] = time.split(':');
    const date = new Date(selectedDate);
    date.setHours(Number(hours))
    date.setMinutes(Number(minutes))

    return date.toISOString();
  }

  timeOptions: string[] = [
    '00:00', '00:15', '00:30', '00:45', '01:00', '01:15', '01:30', '01:45', '02:00', '02:15', '02:30', '02:45', 
    '03:00', '03:15', '03:30', '03:45', '04:00', '04:15', '04:30', '04:45', '05:00', '05:15', '05:30', '05:45', 
    '06:00', '06:15', '06:30', '06:45', '07:00', '07:15', '07:30', '07:45', '08:00', '08:15', '08:30', '08:45', 
    '09:00', '09:15', '09:30', '09:45', '10:00', '10:15', '10:30', '10:45', '11:00', '11:15', '11:30', '11:45', 
    '12:00', '12:15', '12:30', '12:45', '13:00', '13:15', '13:30', '13:45', '14:00', '14:15', '14:30', '14:45', 
    '15:00', '15:15', '15:30', '15:45', '16:00', '16:15', '16:30', '16:45', '17:00', '17:15', '17:30', '17:45', 
    '18:00', '18:15', '18:30', '18:45', '19:00', '19:15', '19:30', '19:45', '20:00', '20:15', '20:30', '20:45', 
    '21:00', '21:15', '21:30', '21:45', '22:00', '22:15', '22:30', '22:45', '23:00', '23:15', '23:30', '23:45'
  ];

  sortByDate(a: Event, b: Event) {
    const dateA = new Date(a.selectedDate)
    const dateB = new Date(b.selectedDate);

    if (dateA < dateB) {
      return -1; 
    } else if (dateA > dateB) {
      return 1; 
    } else {
      return 0; 
    }
  }
}
