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
    playerPhoto : Blob,
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