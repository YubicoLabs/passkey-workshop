const Utils = {
  timeoutUtil,
  convertDate,
  cardName
}

const name_hash = [
  "Copay Wright",
  "Mr. Monkey",
  "Sailor Twift",
  "Samuel Lux",
  "Finn the Human",
  "Mardy Bum",
  "Yub Ico",
  "Dr. Toboggan",
]

async function timeoutUtil(timeout) {
  await new Promise((resolve) => {
    setTimeout(() => resolve(), timeout);
  })
}

function convertDate(myDate) {
  return new Date(myDate).toLocaleString();
}

function cardName(userHandle) {
  var hash = 0;
  if(userHandle.length === 0) {
    return hash;
  }
  for(let i = 0; i < userHandle.length; i++) {
    let chr = userHandle.charCodeAt(i); 
    hash += chr;
  }

  return name_hash[hash % 8];
}

export default Utils;