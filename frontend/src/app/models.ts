export interface Login {
    username: string,
    password: string
}

export interface Signup {
    email: string,
    username: string,
    password: string
}

export interface BlockDate {
    from: Date,
    to: Date
}

export interface PlayerInfo {
    name : string,
    weight : number,
    height : number, 
    playerPhoto : string, // received from Backend as encodedString
    email : string,
    phoneNumber : number,
    emergencyContact : number,
    emergencyName : string, 
    address : string,
    pastInjuries : string,
    role : string,
    yearJoined : number
    positions : string[],
    dob : string
}

export interface PlayerProfile {
    id: string,
    email: string,
    name: string,
    playerPhoto: string,
    positions: string[]
}

export interface PlayerStats {
    cap: number, 
    avgShootingPercent: number, 
    avgInterceptionPerGame: number,
    lastUpdated: Date
}

export interface GameData {
    game_id: number,
    label: string,
    against: string,
    date: Date, 
    photoUrl: string[], 
    videoUrl: string[] 
}

export interface QuarterData {
    gs: string;
    ga: string;
    wa: string;
    c: string;
    wd: string;
    gd: string;
    gk: string;
    duration: number;

    ownScore: number;   
    oppScore: number;
    gaShotIn: number;
    gsShotIn: number;
    gaTotalShots: number;
    gsTotalShots: number;
    ownCpCount: number;
    oppCpCount: number;
    oppSelfError: number;
    goodTeamD: number;
    oppMissShot: number;
    interception: Map<string, number> // convert to json object before sending over to backend 
    interceptions: object
    lostSelfError: number;
    lostByIntercept: number;
    quarterSequence: string[][]
}

export interface CP {
    count: number,
    neat: number,
    returned: number, 
    lost: number
}

export interface WeatherForecast {
    start: string,
    end: string,
    area: string,
    forecast: string
}

export interface Event {
    eventId: string,
    selectedDate: Date,
    title: string,
    startTime: string,
    endTime: string
}

export interface Notif {
    imageUrl: string,
    name: string, // shiyi
    action: string, // added a new event to the team's schedule OR cancelled an event on the team's schedule
    date: Date
}

export interface EmailRequest {
    to: string[],
    subject: string,
    body: string
}

export interface TeamFund {
    id: string,
    name: string,
    paid: boolean
}

export interface EventResult {
    eventId: string,
    response: string
}

export interface EventData {
    results: EventResult[], 
    username: string
}

export interface EventGroupAttd {
    eventId: string,
    response: string, 
    username: string
}

export interface SearchResult {
    publishedAt: string,
    title: string,
    description: string,
    thumbnail: string,
    videoUrl: string
}

export interface APIResponse {
    items: Array<{
      id: {
        videoId: string;
      };
      snippet: {
        publishedAt: string;
        title: string;
        description: string;
        thumbnails: {
          default: { url: string };
        };
      };
    }>;
  }