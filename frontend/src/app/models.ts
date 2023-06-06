export interface Login {
    username: string,
    password: string
}

export interface BlockDate {
    from: Date,
    to: Date
}

export interface PlayerInfo {
    //TODO: add model 
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
    name: string,
    playerPhoto: string,
    positions: string[]
}

export interface GameData {
    label: string,
    against: string,
    date: Date
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
    interceptions: Map<string, number> 
    lostSelfError: number;
    lostByIntercept: number;
    quarterSequence: string[][]
}