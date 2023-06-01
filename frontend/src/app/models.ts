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

    Q1_Players: string[] //
}

export interface QuarterData {

}